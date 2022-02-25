package com.example.wangzheng.widget.multi_input;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Create by wangzheng on 2019/1/16
 */
public class ListenKeyboardLayout extends LinearLayout {
    private int mLastHeight = 0;

    public ListenKeyboardLayout(Context context) {
        this(context, null);
    }

    public ListenKeyboardLayout(Context context,
                                AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        //在输入面板测量之前，得到键盘的状态
        int height = MeasureSpec.getSize(heightMeasureSpec);
        listenKeyboardActive(height, mLastHeight);
        this.mLastHeight = height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h,
                                 int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //在输入面板测量之后，得到键盘的状态
        //listenKeyboardActive(h, oldh);
    }
    boolean isShowKeyboard = false;
    private void listenKeyboardActive(int height,
                                      int lastHeight) {
        if (lastHeight == 0) {
            return;
        }
        int offset = height - lastHeight;
        if (offset == 0) {
            return;
        }
        if (Math.abs(offset) < Toolkit
                .getMinKeyboardHeight()) {
            return;//判定键盘活动的标准
        }
        isShowKeyboard = offset < 0;
        if (mSwitchListener != null) {
            mSwitchListener.onSwitch(
                    isShowKeyboard);
        }
        Toolkit.saveKeyboardHeight(Math.abs(offset));
    }

    private SwitchListener mSwitchListener;

    public void setSwitchListener(
            SwitchListener switchListener) {
        mSwitchListener = switchListener;
    }

    public interface SwitchListener {
        void onSwitch(boolean isShow);
    }
}
