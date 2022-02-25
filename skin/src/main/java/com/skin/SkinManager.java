package com.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Observable;

/**
 * Created by wangzheng on 2017/12/20.
 */

public class SkinManager extends Observable {
    private static volatile SkinManager instance;

    private SkinManager() {
    }

    //double check lock single
    public final static SkinManager getInstance() {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager();
                }
            }
        }
        return instance;
    }

    //-----------------------------------------------------------------------------------------------

    private Resources mSkinResources;
    private String mPackageName;
    private Context mContext;

    public void load(Context context, String skinPath) {
        load(context, skinPath, null);
    }

    public void load(Context context, String skinPath, SkinApplyListener listener) {
        mContext = context.getApplicationContext();

        File file = new File(skinPath);
        if (!file.exists()) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(
                skinPath, PackageManager.GET_ACTIVITIES);
        mPackageName = packageInfo.packageName;

        //这里是换肤的核心代码了，不同的皮肤包创建不同的 AssetManager
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass()
                    .getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, skinPath);

            Method ensureStringBlocks = AssetManager.class
                    .getDeclaredMethod("ensureStringBlocks");
            ensureStringBlocks.setAccessible(true);
            ensureStringBlocks.invoke(assetManager);

            Resources superResources = context.getResources();
            mSkinResources = new Resources(assetManager,
                    superResources.getDisplayMetrics(),
                    superResources.getConfiguration());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            mPackageName = null;
            mSkinResources = null;
        }

        if (mSkinResources == null) {
            if (listener != null) {
                listener.onFailed();
            }
        } else {
            if (listener != null) {
                listener.onSuccess();
            }
            notifyObservers(mSkinResources);
        }
    }


    public int getColor(SkinAttr attr) {
        int color = ContextCompat.getColor(mContext, attr.resId);
        if (mSkinResources == null) {
            return color;
        }

        int skinId = mSkinResources.getIdentifier(
                attr.resName, attr.resType, mPackageName);
        try {
            color = mSkinResources.getColor(skinId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }

    public ColorStateList colorStateList(SkinAttr attr) {
        ColorStateList colorStateList = ContextCompat.getColorStateList(mContext, attr.resId);
        if (mSkinResources == null) {
            return colorStateList;
        }

        int skinId = mSkinResources.getIdentifier(
                attr.resName, attr.resType, mPackageName);
        if (skinId != 0) {
            colorStateList = mSkinResources.getColorStateList(skinId);
        }
        return colorStateList;
    }

    public Drawable getDrawable(SkinAttr attr) {
        Drawable drawable = ContextCompat.getDrawable(mContext, attr.resId);
        if (mSkinResources == null) {
            return drawable;
        }

        int skinId = mSkinResources.getIdentifier(
                attr.resName, attr.resType, mPackageName);
        try {
            if (android.os.Build.VERSION.SDK_INT
                    < Build.VERSION_CODES.LOLLIPOP) {
                drawable = mSkinResources.getDrawable(skinId);
            } else {
                drawable = mSkinResources.getDrawable(skinId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawable;
    }

    public boolean canApplySkin() {
        return mSkinResources != null;
    }

    public interface SkinApplyListener {
        void onSuccess();

        void onFailed();
    }
}
