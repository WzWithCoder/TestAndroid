package com.example.wangzheng.common;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.wangzheng.App;
import com.example.wangzheng.R;
import com.example.wangzheng.widget.viewpager.AutoScrollFactorScroller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A collection of utility methods, all static.
 */
public class CommonKit {
    public static void toast(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(App.instance(), msg,
                Toast.LENGTH_SHORT).show();
    }

    public static void palyRingtone() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone ringtone = RingtoneManager.getRingtone(App.instance(), uri);
        ringtone.play();
    }

    //获取target在parent中的坐标out
    public static void getLocOnView(ViewGroup parent,
                                    View target,
                                    Point out) {
        if (parent == null || target == null ||
                parent == target) {
            return;
        }
        View view = (View) target.getParent();
        out.x += /*view.getScrollX() -*/ target.getLeft();
        out.y += /*view.getScrollY() -*/ target.getTop();
        getLocOnView(parent, view, out);
    }

    public static void recursionPrint(View root) {
        printView(root);
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                recursionPrint(childView);
            }
        }
    }

    public static void breadthFirst(View view,Callable<View, Boolean> callable) {
        LinkedList<View> viewDeque = new LinkedList();
        viewDeque.push(view);
        while (!viewDeque.isEmpty()) {
            view = viewDeque.poll();
            printView(view);
            if (callable.call(view)) return;
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View childView = viewGroup.getChildAt(i);
                    viewDeque.addLast(childView);
                }
            }
        }
    }

    public static void depthFirst(View view,Callable<View, Boolean> callable) {
        LinkedList<View> viewDeque = new LinkedList();
        viewDeque.push(view);
        while (!viewDeque.isEmpty()) {
            view = viewDeque.pop();
            printView(view);
            if (callable.call(view)) return;
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();
                for (int i = 0; i < count; i++) {
                    View childView = viewGroup.getChildAt(i);
                    viewDeque.push(childView);
                }
            }
        }
    }

    private static void printView(View view) {
        Log.d("printView", view.toString());
    }

    public static int getStatusBarHeight() {
        int height = 19;
        Resources resources = App.instance().getResources();
        int resourceId = resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static RectF getRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1]
                , location[0] + view.getWidth()
                , location[1] + view.getHeight());
    }

    public static Point getScreenSize(Point outSize) {
        Display display = ((WindowManager) App.instance()
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        if (outSize == null) {
            outSize = new Point();
        }
        display.getSize(outSize);
        return outSize;
    }

    public static int getScreenWidth() {
        Point outSize = getScreenSize(null);
        return outSize.x;
    }

    public static int getScreenHeight() {
        Point outSize = getScreenSize(null);
        return outSize.y;
    }

    public static int dip2px(float dpValue) {
        return dip2px(App.instance(), dpValue);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        Context context = App.instance();
        final float fontScale = context.getResources()
                .getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String formatNumber(double value) {
        DecimalFormat df = new DecimalFormat("#,##0.0");
        return df.format(value);
    }

    public static Activity canForActivity(Context context) {
        if (context == null)
            return null;
        else if (context instanceof Activity)
            return (Activity) context;
        else if (context instanceof ContextWrapper)
            return canForActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }

    public static boolean canNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                App.instance().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    // 系统的Density
    private static float sNoncompatDensity;
    // 系统的ScaledDensity
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(Activity activity, final Application application) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = displayMetrics.density;
            sNoncompatScaledDensity = displayMetrics.scaledDensity;
            //监听在系统设置中切换字体
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application
                                .getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        // 此处以360dp的设计图作为例子
        float targetDensity = displayMetrics.widthPixels / 360;
        float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        int targetDensityDpi = (int) (160 * targetDensity);
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    public static int colorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(
                R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static int darkColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(
                R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    public static int colorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(
                R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取最近的照片
     *
     * @param context
     * @param time    最近time秒
     * @return image path
     */
    public static String getRecentImage(Context context, long time) {
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        };
        String selection = MediaStore.Images.Media.DATE_ADDED + " >= ?";
        String[] selectionArgs = new String[]{String.valueOf(time)};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DATA);
            return cursor.getString(columnIndex);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public static void setScrollFactor(ViewPager viewPager, double factor) throws Exception {
        Class<ViewPager> clazz = ViewPager.class;
        Field mScroller = clazz.getDeclaredField("mScroller");
        mScroller.setAccessible(true);
        Field sInterpolator = clazz.getDeclaredField("sInterpolator");
        sInterpolator.setAccessible(true);
        Interpolator interpolator = (Interpolator)
                sInterpolator.get(viewPager);
        AutoScrollFactorScroller scroller = new AutoScrollFactorScroller(
                viewPager.getContext(), interpolator);
        scroller.setFactor(factor);
        mScroller.set(viewPager, scroller);
    }

    public static int makeAlphaWithColor(int color) {
        return (color >> 24) & 0xff;
    }

    public static String getIMEI() {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("adb shell service call iphonesubinfo 4");
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str.trim();
    }

    public static boolean isPointInView(View v, MotionEvent event) {
        if (v == null) return false;
        Rect outRect = new Rect();
        v.getGlobalVisibleRect(outRect);
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        Log.e("cdcdcdc", outRect.toString()+"-----"+x+":"+y+"===="+outRect.contains(x , y));
        return outRect.contains(x , y);
    }

    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }
}
