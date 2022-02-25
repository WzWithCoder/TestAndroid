package com.example.wangzheng.widget.multi_input;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;


/**
 * Create by wangzheng on 2019/1/15
 */
public class MultiInputWindow {
    private Activity          mContext;
    private PopupWindow       mPopupWindow;
    private CheckBox          mSwitchButton;
    private TextView          mSendButton;
    private EditText          mInputView;
    private MultiPanelLayout mPanelContainer;


    public static MultiInputWindow show(Activity context) {
        MultiInputWindow inputWindow =
                new MultiInputWindow(context);
        inputWindow.show();
        return inputWindow;
    }

    private void show() {
        mPopupWindow.showAtLocation(mPanelContainer
                , Gravity.BOTTOM, 0, 0);
        mPanelContainer.showKeyboard(mInputView);
    }

    public MultiInputWindow(Activity context) {
        this.mContext = context;

        View rootView = View.inflate(context,
                R.layout.dialog_multi_input_layout, null);
        mPanelContainer = rootView.findViewById(R.id.panel_container);
        mInputView = rootView.findViewById(R.id.input_container);
        mSwitchButton = rootView.findViewById(R.id.switch_input);
        mSendButton = rootView.findViewById(R.id.send_button);

        mPopupWindow = new PopupWindow(rootView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                true);
        mPopupWindow.setAnimationStyle(R.style.multiinput_anim_style);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(
                ()-> mPanelContainer.cancel(mInputView));

        mSwitchButton.setOnCheckedChangeListener((v, isChecked)-> {
            mPanelContainer.switchPanel(isChecked ?
                    View.VISIBLE : View.GONE,
                    mInputView);
        });

        mInputView.setOnTouchListener((v, event)-> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                mSwitchButton.setChecked(false);
            }
            return false;
        });

        mSendButton.setOnClickListener(v-> {
            String text = mInputView.getText().toString();
            CommonKit.toast(text);
        });

        mPanelContainer.setSwitchListener(bool-> {
            if (mPanelContainer.isCancel()) {
                mPopupWindow.dismiss();
            }
        });

        rootView.findViewById(R.id.lucency_layer)
                .setOnClickListener(v-> {
                    mPopupWindow.dismiss();
                });
    }
}
