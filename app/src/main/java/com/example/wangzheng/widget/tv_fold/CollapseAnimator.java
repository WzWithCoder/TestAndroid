package com.example.wangzheng.widget.tv_fold;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Create by wangzheng on 12/9/20
 */
public final class CollapseAnimator extends Animation {
    private View target;
    private int start;
    private int end;

    public CollapseAnimator(View target) {
        this.target = target;
        setFillAfter(true);
    }

    public static CollapseAnimator with(View target) {
        return new CollapseAnimator(target);
    }

    public CollapseAnimator of(int start, int end) {
        this.start = start;
        this.end = end;
        applyDuration();
        return this;
    }

    public final void applyDuration() {
        float speed = calculateSpeedPerPixel();
        float time = Math.abs(end - start) * speed;
        time = Math.min(time, 618);
        setDuration((long) time);
    }

    public CollapseAnimator listener(AnimationListener listener) {
        setAnimationListener(listener);
        return this;
    }

    public final void startAnimator() {
        if (canStartAnimator()) {
            return;
        }
        target.clearAnimation();
        target.startAnimation(this);
    }

    public final boolean canStartAnimator() {
        Animation animation = target.getAnimation();
        return animation != null &&
                !animation.hasEnded();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (end - start) * interpolatedTime + start;
        target.getLayoutParams().height = (int) (height);
        target.setScrollY(0);
        target.requestLayout();
    }

    protected final float calculateSpeedPerPixel() {
        Context context = target.getContext();
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources
                .getDisplayMetrics();
        //默认单位速度 300F/densityDpi
        //滑动一英寸的时间除以 显示器的逻辑密度，即每个px的滑动时间
        final float timeInInch = 618F;
        return timeInInch / displayMetrics.densityDpi;
    }
}