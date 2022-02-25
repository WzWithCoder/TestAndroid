package com.example.wangzheng.widget.multi_input;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.SpKit;
import com.example.wangzheng.common.TextWatcher;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Create by wangzheng on 2019/1/15
 */
public class MultiInputWindow1 extends DialogFragment implements KeyboardListener.OnKeyboardListener {
    private static String key = "keyborad-height";
    private static int mKeyBoardHeight;

    private CheckBox    mSwitchButton;
    private TextView    mSendButton;
    private EditText    mInputView;
    private FrameLayout mPanelContainer;
    private View        mPanel2;

    public static MultiInputWindow1 show(Activity context) {
        MultiInputWindow1 inputWindow = new MultiInputWindow1();
        FragmentActivity activity = (FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();
        inputWindow.show(fm, "xxxx");
        return inputWindow;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.InputDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_multi_input_layout1, container);
        onViewCreated1(view, savedInstanceState);
        return view;
    }

    public void onViewCreated1(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPanelContainer = view.findViewById(R.id.panel_container);
        mInputView = view.findViewById(R.id.input_container);
        mSwitchButton = view.findViewById(R.id.switch_input);
        mSendButton = view.findViewById(R.id.send_button);
        mPanel2 = view.findViewById(R.id.panel2);

        ImageView addButton = view.findViewById(R.id.add_button);
        mInputView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                addButton.setVisibility(s.length() <= 0 ?
                        VISIBLE : View.GONE);
                mSendButton.setVisibility(s.length() > 0 ?
                        VISIBLE : View.GONE);
            }
        });

        addButton.setOnClickListener(v-> {
            View panel = view.findViewById(R.id.panel2);
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

        view.findViewById(R.id.shade_layer)
                .setOnClickListener(v-> {
                    dismiss();
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

        KeyboardListener.on(view, this);
        KeyboardListener.on(view, keyBoardDismiss());

        mKeyBoardHeight = SpKit.read(key, 0);
        view.post(()-> doTranslationY());
        mPanel2.setMinimumHeight(mKeyBoardHeight);

        mSendButton.setOnClickListener(v-> {
            String text = mInputView.getText().toString();
            CommonKit.toast(text);
        });

        view.post(()-> {
            mSwitchButton.setChecked(true);
            Toolkit.showInputMethod(mInputView);
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
        View view = getView();
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
        View view = getView();
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
                getActivity().isInMultiWindowMode();
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
                    dismiss();
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
