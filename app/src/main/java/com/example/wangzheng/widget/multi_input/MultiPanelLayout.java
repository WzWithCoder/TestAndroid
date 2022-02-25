package com.example.wangzheng.widget.multi_input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Create by wangzheng on 2019/1/16
 */
public class MultiPanelLayout extends LinearLayout
        implements ListenKeyboardLayout.SwitchListener {

    //region 怎样保证isHidePanel和isShowKeyboard状态的同步？
    private boolean isHidePanel = true;
    private boolean isShowKeyboard = false;
    //endregion

    public MultiPanelLayout(Context context) {
        this(context, null);
    }

    public MultiPanelLayout(Context context,
                            AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) return;
        if (!Toolkit.installKeyboardListener(this)) {
            String message = "not found ListenKeyboardLayout";
            throw new InflateException(message);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = Toolkit.getKeyboardHeight();
        if (isShowKeyboard || isHidePanel) {
            height = 0;
        }
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                height, View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void switchPanel(int flag, View anchor) {
        if (flag == View.VISIBLE) {
            showPanel(anchor);
        } else {
            showKeyboard(anchor);
        }
    }

    public void showKeyboard(View anchor) {
        isHidePanel = true;
        anchor.requestFocus();
        Toolkit.showInputMethod(anchor);
    }

    public void showPanel(View anchor) {
        isHidePanel = false;
        anchor.clearFocus();
        hideOrRemeasure(anchor);
    }

    public void cancel(View anchor) {
        isHidePanel = true;
        anchor.clearFocus();
        hideOrRemeasure(anchor);
    }

    private void hideOrRemeasure(View anchor) {
        if (isShowKeyboard) {
            Toolkit.hideInputMethod(anchor);
        } else {
            requestLayout();
            onSwitch(isShowKeyboard);
        }
    }

    public boolean isCancel(){
        return isHidePanel && !isShowKeyboard;
    }

    @Override
    public void onSwitch(boolean isShow) {
        isShowKeyboard = isShow;
        if (mSwitchListener != null) {
            mSwitchListener.onSwitch(
                    isShowKeyboard);
        }
    }

    private ListenKeyboardLayout.SwitchListener mSwitchListener;

    public void setSwitchListener(
            ListenKeyboardLayout.SwitchListener switchListener) {
        this.mSwitchListener = switchListener;
    }
}
