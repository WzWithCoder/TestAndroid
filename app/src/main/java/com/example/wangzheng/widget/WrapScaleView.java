package com.example.wangzheng.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

import com.example.wangzheng.plugin.ReflectKit;

/**
 * Create by wangzheng on 2018/2/11
 */

public class WrapScaleView<T extends ViewGroup.MarginLayoutParams> {
    private T mLayoutParams;
    private View mContent;

    public WrapScaleView(View content) throws Exception {
        mContent = content;
        mLayoutParams = (T) mContent.getLayoutParams();
        if (mLayoutParams == null) {
            ViewParent viewParent = content.getParent();
            mLayoutParams = (T) ReflectKit.invokeMethod(viewParent,
                    viewParent.getClass(), "generateDefaultLayoutParams");
            mContent.setLayoutParams(mLayoutParams);
        }
    }

    public static WrapScaleView from(View content) {
        try {
            return new WrapScaleView(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void applyRange(RectF rect) {
        mLayoutParams.topMargin = (int) rect.top;
        mLayoutParams.leftMargin = (int) rect.left;
        mLayoutParams.width = (int) rect.width();
        mLayoutParams.height = (int) rect.height();
        mContent.requestLayout();
    }

    public ValueAnimator animator(RectF fromRect, final RectF toRect,
                                  Animator.AnimatorListener animatorListener,
                                  final ValueAnimator.AnimatorUpdateListener updateListener) {
        applyRange(fromRect);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setValues(
                PropertyValuesHolder.ofFloat("top", fromRect.top, toRect.top),
                PropertyValuesHolder.ofFloat("left", fromRect.left, toRect.left),
                PropertyValuesHolder.ofFloat("width", fromRect.width(), toRect.width()),
                PropertyValuesHolder.ofFloat("height", fromRect.height(), toRect.height()));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mLayoutParams.topMargin = (int) animation.getAnimatedValue("top");
                mLayoutParams.leftMargin = (int) animation.getAnimatedValue("left");
                mLayoutParams.width = (int) animation.getAnimatedValue("width");
                mLayoutParams.height = (int) animation.getAnimatedValue("height");
                mContent.requestLayout();
                if (updateListener != null) {
                    updateListener.onAnimationUpdate(animation);
                }
            }
        });
        if (animatorListener != null) {
            valueAnimator.addListener(animatorListener);
        }
        valueAnimator.start();
        return valueAnimator;
    }

    private void startAnimator(final View view, Rect inRect, final Rect outRect, boolean shrink) {
        float pivotX = inRect.width() * 1f
                * (inRect.left)
                / (outRect.width() - inRect.width());
        view.setPivotX(pivotX);

        float transY = 0;
        float pivotY = 0;
        if (outRect.contains(inRect)) {
            pivotY = inRect.height() * 1f
                    * (inRect.top - outRect.top)
                    / (outRect.height() - inRect.height());
        } else {
            transY = outRect.top - inRect.top;
        }
        view.setPivotY(pivotY);

        float originX = 1f, originY = 1f;
        float scaleX = outRect.width() * 1f / inRect.width();
        float scaleY = outRect.height() * 1f / inRect.height();

        if (shrink) {
            originX = scaleX;
            scaleX = 1f;
            originY = scaleY;
            scaleY = 1f;
            transY = 0;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationY", transY),
                ObjectAnimator.ofFloat(view, "scaleX", originX, scaleX),
                ObjectAnimator.ofFloat(view, "scaleY", originY, scaleY));
        animatorSet.setDuration(500);
        animatorSet.start();
    }
}
