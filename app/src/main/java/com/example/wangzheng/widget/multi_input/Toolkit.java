package com.example.wangzheng.widget.multi_input;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.example.wangzheng.common.Call;
import com.example.wangzheng.common.CallHandler;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.SpKit;

/**
 * Create by wangzheng on 2019/1/16
 */
public class Toolkit {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        int height = 19;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void showInputMethod(View viewToken) {
        final Context context = viewToken.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(viewToken, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void showOrHideInputMethod(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
    }

    public static void hideInputMethod(View viewToken) {
        final Context context = viewToken.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewToken.getWindowToken(), 0);
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isTranslucentStatus(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (activity.getWindow().getAttributes().flags
                    & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
        }
        return false;
    }

    public static boolean installKeyboardListener(MultiPanelLayout panel) {
        ViewParent target = panel;
        do {
            target = target.getParent();
            if (target == null) {
                break;
            }
        } while (!(target instanceof ListenKeyboardLayout));

        if (target instanceof ListenKeyboardLayout) {
            ((ListenKeyboardLayout) target)
                    .setSwitchListener(panel);
            return true;
        }
        return false;
    }

    public static void listenKeyboard(Activity context, Call<Boolean> call) {
        KeyboardListener.on(context, ((height, isVisible) -> call.call(isVisible)));
    }

    public static boolean measureKeyboard(View anchor) {
        View rootView = anchor.getRootView();
        int screenHeight = rootView.getHeight();

        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int contentHeight = rect.bottom - rect.top;

        int height = screenHeight - contentHeight;

        Activity activity = (Activity) anchor.getContext();

        if (!(isTranslucentStatus(activity)
                || isFullScreen(activity))) {
            height -= getStatusBarHeight(activity);
        }
        if (height < getMinKeyboardHeight()) {
            return false;
        }

        saveKeyboardHeight(height);
        return true;
    }

    private static final String KEY_BOARD_HEIGHT = "KeyboardHeight";

    private static int mKeyboardHeight = getKeyboardHeight();

    public static int getMinKeyboardHeight() {
        return CommonKit.dip2px(150);
    }

    public static int getKeyboardHeight() {
        if (mKeyboardHeight > 0) {
            return mKeyboardHeight;
        }
        int defValue = getMinKeyboardHeight();
        return getKeyboardHeight(defValue);
    }

    public static int getKeyboardHeight(int defValue) {
        return SpKit.read(KEY_BOARD_HEIGHT, defValue);
    }

    public static void saveKeyboardHeight(int value) {
        if (mKeyboardHeight == value) return;
        mKeyboardHeight = value;
        SpKit.write(KEY_BOARD_HEIGHT, value);
    }
}
