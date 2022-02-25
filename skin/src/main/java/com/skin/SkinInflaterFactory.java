package com.skin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wangzheng on 2017/12/20.
 */

public class SkinInflaterFactory implements LayoutInflater.Factory2, Observer {
    private final String skinNameSpace = "http://schemas.android.com/skin";
    private  final String skinEnableAttr = "enableSkin";
    private List<SkinTarget> targets = new ArrayList<>();

    public SkinInflaterFactory() {
        SkinManager.getInstance().addObserver(this);
    }

    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        try {
            view = createView(context, name, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isSkinEnable = attrs.getAttributeBooleanValue(
                skinNameSpace, skinEnableAttr, false);
        if (view != null && isSkinEnable) {
            inflateSkinAttrs(context, attrs, view);
        }
        return view;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            view = createView(context, name, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isSkinEnable = attrs.getAttributeBooleanValue(
                skinNameSpace, skinEnableAttr, false);
        if (view != null && isSkinEnable) {
            inflateSkinAttrs(context, attrs, view);
        }
        return view;
    }

    private View createView(Context context, String name, AttributeSet attrs) throws Exception {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (-1 == name.indexOf('.')) {
            if ("View".equals(name)) {
                view = inflater.createView(name, "android.view.", attrs);
            }
            if (view == null) {
                view = inflater.createView(name, "android.widget.", attrs);
            }
            if (view == null) {
                view = inflater.createView(name, "android.webkit.", attrs);
            }
        } else {
            view = inflater.createView(name, null, attrs);
        }
        return view;
    }

    private void inflateSkinAttrs(Context context, AttributeSet attrs, View view) {
        List<SkinAttr> skinAttrs = new ArrayList<>();

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //attrName = textColor, attrValue = @2131099660
            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);
            Log.e(view.getClass().getSimpleName(), attrName + "-" + attrValue);
            if (!AttrEnum.contain(attrName)) {
                continue;
            }

            SkinAttr skinAttr = null;
            try {
                skinAttr = parseSkinAttr(context, attrName, attrValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (skinAttr != null) {
                skinAttrs.add(skinAttr);
                Log.e(view.getClass().getSimpleName(), skinAttr.toString());
            }
        }

        if (!skinAttrs.isEmpty()) {
            SkinTarget skinTarget = new SkinTarget();
            skinTarget.view = view;
            skinTarget.attrs = skinAttrs;
            targets.add(skinTarget);

            if (SkinManager.getInstance().canApplySkin()) {
                skinTarget.apply();
            }
        }
    }

    private SkinAttr parseSkinAttr(Context context, String attrName
            , String attrValue) throws Exception {
        //id值是 @ + 数值
        if (!attrValue.startsWith("@")) return null;
        int resId = Integer.parseInt(attrValue.substring(1));
        String resName = context.getResources().getResourceEntryName(resId);
        String resType = context.getResources().getResourceTypeName(resId);
        return SkinAttr.from(attrName, resId, resName, resType);
    }

    @Override
    public void update(Observable o, Object arg) {
        for (SkinTarget target : targets) {
            target.apply();
        }
    }
}
