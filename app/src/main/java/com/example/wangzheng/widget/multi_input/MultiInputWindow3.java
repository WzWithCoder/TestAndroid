package com.example.wangzheng.widget.multi_input;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.SpKit;
import com.example.wangzheng.common.TextWatcher;


/**
 * Create by wangzheng on 2019/1/15
 */
public class MultiInputWindow3 implements KeyboardListener.OnKeyboardListener {
    private static String key = "keyborad-height";
    private static int mKeyBoardHeight;

    private Activity    mContext;
    private PopupWindow mPopupWindow;
    private CheckBox    mSwitchButton;
    private TextView    mSendButton;
    private EditText    mInputView;
    private FrameLayout mPanelContainer;
    private View        mPanel2;

    public static MultiInputWindow3 show(Activity context) {
        MultiInputWindow3 inputWindow =
                new MultiInputWindow3(context);
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

    public MultiInputWindow3(Activity context) {
        this.mContext = context;

        int resource = R.layout.dialog_multi_input_layout1;
        View contentView = View.inflate(context, resource, null);
        mPanelContainer = contentView.findViewById(R.id.panel_container);
        mInputView = contentView.findViewById(R.id.input_container);
        mSwitchButton = contentView.findViewById(R.id.switch_input);
        mSendButton = contentView.findViewById(R.id.send_button);
        mPanel2 = contentView.findViewById(R.id.panel2);

        mPopupWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                true);
        mPopupWindow.setAnimationStyle(R.style.multiinput_anim_style);
        mPopupWindow.setInputMethodMode(INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(SOFT_INPUT_ADJUST_NOTHING);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(()-> {
            Toolkit.hideInputMethod(mInputView);
        });

        ImageView addButton = contentView.findViewById(R.id.add_button);
        mInputView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                addButton.setVisibility(s.length() <= 0 ?
                        VISIBLE : View.GONE);
                mSendButton.setVisibility(s.length() > 0 ?
                        VISIBLE : View.GONE);
            }
        });

        addButton.setOnClickListener(v-> {
            View panel = contentView.findViewById(R.id.panel2);
            if (panel.isShown()) {
                if (mSwitchButton.isChecked()) {
                    toggleInputPanel(true, -1);
                } else {
                    mSwitchButton.setChecked(true);
                }
            } else {
                toggleInputPanel(false, R.id.panel2);
            }
        });

        contentView.findViewById(R.id.shade_layer)
                .setOnClickListener(v-> {
                    mPopupWindow.dismiss();
                });

        mInputView.setOnClickListener(v-> {
            if (mSwitchButton.isChecked()) {
                toggleInputPanel(true, -1);
            } else {
                mSwitchButton.setChecked(true);
            }
        });

        mSwitchButton.setOnCheckedChangeListener((v, isVisible)-> {
            int id = isVisible ? -1 : R.id.panel1;
            //动画放在这是考虑分屏模式下监听不到键盘动态
            toggleInputPanel(isVisible, id);
        });

        KeyboardListener.on(contentView, this);
        KeyboardListener.on(contentView, keyBoardDismiss());

        mKeyBoardHeight = SpKit.read(key, 0);
        contentView.post(()-> doTranslationY());
        mPanel2.setMinimumHeight(mKeyBoardHeight);

        mSendButton.setOnClickListener(v-> {
            String text = mInputView.getText().toString();
            CommonKit.toast(text);
        });
    }

    @Override
    public void onChangeVisible(int height, boolean isVisible) {
        final int last = mKeyBoardHeight;
        mKeyBoardHeight = height > 0 ? height : mKeyBoardHeight;
        if (isVisible && last != height && !isInMultiWindowMode()) {
            SpKit.write(key, mKeyBoardHeight);
            //首次补充动画和键盘高度调整
            doAnimator(isVisible);
        }
    }

    public void toggleInputPanel(boolean isVisible, int id) {
        toggleSoftInput(isVisible);
        switchInputPanel(id);
        //动画放在这是考虑分屏模式下监听不到键盘动态
        doAnimator(isVisible);
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
            View view = safeFindCurrentPanel();
            int ph = view.getHeight();
            offset = height - ph;
        } else if (isInMultiWindowMode()) {
            offset = height;
        }
        return offset;
    }

    private void switchInputPanel(int id) {
        View lastView = mPanelContainer.findViewById(id);
        View current = findCurrentPanel();
        if (current == lastView) return;
        if (lastView != null) {
            lastView.setVisibility(VISIBLE);
        }
        if (current != null) {
            current.setVisibility(INVISIBLE);
        }
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
                final View panel = findCurrentPanel();
                if (!isVisible && panel == null && lastHeight > 0) {
                    mPopupWindow.dismiss();
                }
                lastHeight = height;
            }
        };
    }

    private View safeFindCurrentPanel() {
        View view = findCurrentPanel();
        if (view != null) {
            return view;
        }
        return mPanelContainer;
    }

    private View findCurrentPanel() {
        int childCount = mPanelContainer.getChildCount();
        View childView = null;
        for (int i = 0; i < childCount; i++) {
            childView = mPanelContainer.getChildAt(i);
            if (childView.getVisibility() == VISIBLE) {
                return childView;
            }
        }
        return null;
    }
}
