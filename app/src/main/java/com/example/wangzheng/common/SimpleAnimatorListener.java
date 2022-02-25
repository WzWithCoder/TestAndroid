package com.example.wangzheng.common;

import android.animation.Animator;
import android.os.Build;

/**
 * Create by wangzheng on 2018/7/30
 */
public class SimpleAnimatorListener implements Animator.AnimatorListener {
    private Animator.AnimatorListener animatorListener;

    public SimpleAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.animatorListener = animatorListener;
    }

    public SimpleAnimatorListener() {
    }

    /**
     * <p>Notifies the start of the animation as well as the animation's overall play direction.
     * This method's default behavior is to call {@link #onAnimationStart(Animator)}. This
     * method can be overridden, though not required, to get the additional play direction info
     * when an animation starts. Skipping calling super when overriding this method results in
     * {@link #onAnimationStart(Animator)} not getting called.
     *
     * @param animation The started animation.
     * @param isReverse Whether the animation is playing in reverse.
     */
    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && animatorListener != null) {
            animatorListener.onAnimationStart(animation, isReverse);
        }
    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     * <p>
     * <p>This method's default behavior is to call {@link #onAnimationEnd(Animator)}. This
     * method can be overridden, though not required, to get the additional play direction info
     * when an animation ends. Skipping calling super when overriding this method results in
     * {@link #onAnimationEnd(Animator)} not getting called.
     *
     * @param animation The animation which reached its end.
     * @param isReverse Whether the animation is playing in reverse.
     */
    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && animatorListener != null) {
            animatorListener.onAnimationEnd(animation, isReverse);
        }
    }

    /**
     * <p>Notifies the start of the animation.</p>
     *
     * @param animation The started animation.
     */
    @Override
    public void onAnimationStart(Animator animation) {
        if (animatorListener != null) {
            animatorListener.onAnimationStart(animation);
        }
    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which reached its end.
     */
    @Override
    public void onAnimationEnd(Animator animation) {
        if (animatorListener != null) {
            animatorListener.onAnimationEnd(animation);
        }
    }

    /**
     * <p>Notifies the cancellation of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which was canceled.
     */
    @Override
    public void onAnimationCancel(Animator animation) {
        if (animatorListener != null) {
            animatorListener.onAnimationCancel(animation);
        }
    }

    /**
     * <p>Notifies the repetition of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationRepeat(Animator animation) {
        if (animatorListener != null) {
            animatorListener.onAnimationRepeat(animation);
        }
    }
}
