package com.example.wangzheng.widget.zoom_imageview;

import android.animation.TypeEvaluator;
import android.graphics.RectF;

public class RectEvaluator implements TypeEvaluator<RectF> {
    private RectF mRect;

    public RectEvaluator() {}

    public RectEvaluator(RectF reuseRect) {
        mRect = reuseRect;
    }

    @Override
    public RectF evaluate(float fraction, RectF startValue, RectF endValue) {
        float left = startValue.left + (endValue.left - startValue.left) * fraction;
        float top = startValue.top + (endValue.top - startValue.top) * fraction;
        float right = startValue.right + (endValue.right - startValue.right) * fraction;
        float bottom = startValue.bottom + (endValue.bottom - startValue.bottom) * fraction;
        if (mRect == null) {
            return new RectF(left, top, right, bottom);
        } else {
            mRect.set(left, top, right, bottom);
            return mRect;
        }
    }

    /*ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration*10);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setValues(
                PropertyValuesHolder.ofFloat("value", fromValue, toValue),
            PropertyValuesHolder.ofObject("color"
            , mArgbEvaluator, srcColor, dstColor),
            PropertyValuesHolder.ofObject("clipRect"
            , new RectEvaluator(), srcClip, dstClip),
            PropertyValuesHolder.ofObject("displayRect"
            , new RectEvaluator(), srcDisplay, dstDisplay));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator animation) {
            mClipRect = (RectF) animation.getAnimatedValue("clipRect");

            mBaseMatrix.setRectToRect(srcRect,
                    (RectF) animation.getAnimatedValue("displayRect"),
                    Matrix.ScaleToFit.CENTER);
            setImageMatrix(mBaseMatrix);

            setBackgroundColor((int) animation.getAnimatedValue("color"));

            if (mAnimatorUpdateListener != null) {
                mAnimatorUpdateListener.onAnimationUpdate(
                        (float) animation.getAnimatedValue("value"));
            }
        }
    });*/

}