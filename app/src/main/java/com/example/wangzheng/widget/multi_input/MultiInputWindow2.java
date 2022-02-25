package com.example.wangzheng.widget.multi_input;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.SpKit;


/**
 * Create by wangzheng on 2019/1/15
 */
public class MultiInputWindow2 implements KeyboardListener.OnKeyboardListener {
    private static String key = "keyborad-height";
    private static int mKeyBoardHeight;

    private Activity    mContext;
    private PopupWindow mPopupWindow;
    private CheckBox    mSwitchButton;
    private TextView    mSendButton;
    private EditText    mInputView;
    private FrameLayout mPanelContainer;

    public static MultiInputWindow2 show(Activity context) {
        MultiInputWindow2 inputWindow =
                new MultiInputWindow2(context);
        inputWindow.show();
        return inputWindow;
    }

    private void show() {
        mPopupWindow.showAtLocation(mPanelContainer
                , Gravity.BOTTOM, 0, 0);
        mPanelContainer.postDelayed(()-> {
            mSwitchButton.setChecked(true);
        }, 50);
    }

    public MultiInputWindow2(Activity context) {
        this.mContext = context;

        int resource = R.layout.dialog_multi_input_layout1;
        View contentView = View.inflate(context, resource, null);
        mPopupWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                true);
        mPopupWindow.setAnimationStyle(R.style.multiinput_anim_style);
        mPopupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());

        mPanelContainer = contentView.findViewById(R.id.panel_container);
        mInputView = contentView.findViewById(R.id.input_container);
        mSwitchButton = contentView.findViewById(R.id.switch_input);
        mSendButton = contentView.findViewById(R.id.send_button);

//        contentView.findViewById(R.id.shade_layer)
//                .setOnClickListener(v-> {
//                    mPopupWindow.dismiss();
//                });

        mInputView.setOnClickListener(v-> {
            mSwitchButton.setChecked(true);
        });

        mSendButton.setOnClickListener(v-> {
            String text = mInputView.getText().toString();
            CommonKit.toast(text);
        });

        mSwitchButton.setOnCheckedChangeListener((v, isVisible)-> {
            toggleSoftInput(isVisible);
            //动画放在这是考虑分屏模式下监听不到键盘动态
            doAnimator(isVisible);
        });

        KeyboardListener.on(contentView, this);
        KeyboardListener.on(contentView, keyBoardDismiss());

        mKeyBoardHeight = SpKit.read(key, 0);
        contentView.post(()-> doTranslationY());
    }

    @Override
    public void onChangeVisible(int height, boolean isVisible) {
        final int last = mKeyBoardHeight;
        mKeyBoardHeight = height > 0 ? height : mKeyBoardHeight;
        if (last != height && height > 0) {
            SpKit.write(key, height);
            //首次补充动画和键盘高度调整
            doAnimator(isVisible);
        }
    }

    private void doAnimator(boolean isVisible) {
        int y = computeTranslationY(isVisible);
        View view = mPopupWindow.getContentView();
        view.animate().setDuration(150)
                .translationY(y)
                .withStartAction(()-> {
                    mInputView.clearFocus();
                })
                .withEndAction(()->{
                    mInputView.requestFocus();
                }).start();
    }

    private void doTranslationY() {
        if (mKeyBoardHeight <= 0) return;
        final boolean isVisible = true;
        int y = computeTranslationY(isVisible);
        View view = mPopupWindow.getContentView();
        view.setTranslationY(y);
    }

    private int computeTranslationY(boolean isVisible) {
        int height = mPanelContainer.getHeight();
        int offset = height - mKeyBoardHeight;
        if (!isVisible) {
            offset = 0;
        } else if (isInMultiWindowMode()) {
            offset = height;
        }
        return offset;
    }

    private boolean isInMultiWindowMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                mContext.isInMultiWindowMode();
    }

    public final void toggleSoftInput(boolean isVisible) {
        if (isVisible) {
            mInputView.requestFocus();
            Toolkit.showInputMethod(mInputView);
        } else {
            Toolkit.hideInputMethod(mInputView);
        }
    }

    private KeyboardListener.OnKeyboardListener keyBoardDismiss() {
        return new KeyboardListener.OnKeyboardListener() {
            protected int lastHeight = 0;
            public void onChangeVisible(int height, boolean isVisible) {
                if (!isVisible && mSwitchButton.isChecked()
                        && lastHeight > 0) {
                    mPopupWindow.dismiss();
                }
                lastHeight = height;
            }
        };
    }
}
