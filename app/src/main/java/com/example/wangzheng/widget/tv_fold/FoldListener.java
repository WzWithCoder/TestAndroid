package com.example.wangzheng.widget.tv_fold;

import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;

/**
 * Create by wangzheng on 4/23/21
 */
public class FoldListener implements Animation.AnimationListener{
    private LayoutParams layoutParams;
    private FoldTextView textView;
    private boolean canFoldText;
    private CharSequence text;

    public FoldListener(FoldTextView textView
            , boolean canFoldText, CharSequence text) {
        this.textView = textView;
        this.canFoldText = canFoldText;
        this.text = text;
        layoutParams = textView.getLayoutParams();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (!canFoldText) {
            update();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        textView.clearAnimation();
        if (canFoldText) {
            update();
        }
    }

    public final void update() {
        textView.update(text, canFoldText);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
