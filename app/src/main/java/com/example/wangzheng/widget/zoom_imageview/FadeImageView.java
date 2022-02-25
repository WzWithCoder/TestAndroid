package com.example.wangzheng.widget.zoom_imageview;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.SimpleAnimatorListener;

/**
 * Create by wangzheng on 2018/7/27
 */
public class FadeImageView extends AppCompatImageView {

    protected Matrix mBaseMatrix  = new Matrix();
    protected Matrix mDrawMatrix  = new Matrix();
    protected Matrix mApplyMatrix = new Matrix();

    protected RectF mDisplayRect  = new RectF();
    protected RectF mBaseRect     = new RectF();
    private   RectF mClipRect     = null;
    private   RectF mThumbRect    = null;
    private   RectF mFitThumbRect = null;

    protected ArgbEvaluator mArgbEvaluator = null;

    protected boolean mEnableAnim   = true;
    private   long    mFadeDuration = 300;

    protected AnimatorUpdateListener mAnimatorUpdateListener;

    public void setAnimatorUpdateListener(AnimatorUpdateListener animatorUpdateListener) {
        this.mAnimatorUpdateListener = animatorUpdateListener;
    }

    public FadeImageView(Context context) {
        this(context, null);
    }

    public FadeImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /*response onKeyDown
        setFocusable(true);
        setFocusableInTouchMode(true);*/

        setScaleType(ScaleType.MATRIX);
        mArgbEvaluator = new ArgbEvaluator();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateBaseMatrix();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateBaseMatrix();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        updateBaseMatrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (mClipRect != null) {
            canvas.clipRect(mClipRect);
            mClipRect = null;
        }
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setThumbRect(RectF rectF) {
        mThumbRect = rectF;
        updateBaseMatrix();
    }

    private void updateBaseMatrix() {
        mBaseMatrix.reset();
        mDrawMatrix.reset();
        mApplyMatrix.reset();
        if (configBounds()) {
            if (mEnableAnim) {
                fadeInAnim();
            } else {
                setImageMatrix(getDrawMatrix());
                setBackgroundColor(0xff000000);
            }
        }
    }

    private boolean configBounds() {
        Drawable drawable = getDrawable();
        if (null == drawable || getWidth() * getHeight() == 0) {
            return false;
        }

        final int dwidth = drawable.getIntrinsicWidth();
        final int dheight = drawable.getIntrinsicHeight();

        RectF vrect = new RectF(0, 0, getWidth(), getHeight());
        RectF drect = new RectF(0, 0, dwidth, dheight);

        if (null != mThumbRect) {
            mFitThumbRect = fitThumbRect(drect, mThumbRect);
        }

        mBaseMatrix.setRectToRect(drect, vrect, Matrix.ScaleToFit.CENTER);
        if (drawable instanceof BitmapsDrawable) {
            float scale = Math.max(vrect.width() / drect.width()
                    , vrect.height() / drect.height());
            mBaseMatrix.setScale(scale, scale);
        }
        mBaseRect.set(drect);
        mBaseMatrix.mapRect(mBaseRect);
        return true;
    }

    private RectF fitThumbRect(RectF srcRect, RectF dstRect) {
        float scale;
        float dx = 0, dy = 0;
        if (srcRect.width() * dstRect.height() > dstRect.width() * srcRect.height()) {
            scale = dstRect.height() / srcRect.height();
            dx = dstRect.left - (srcRect.width() * scale - dstRect.width()) * 0.5f;
            dy = dstRect.top;
        } else {
            scale = dstRect.width() / srcRect.width();
            dx = dstRect.left;
            dy = dstRect.top - (srcRect.height() * scale - dstRect.height()) * 0.5f;
        }
        return new RectF(dx, dy,
                dx + srcRect.width() * scale,
                dy + srcRect.height() * scale);
    }

    protected void animator(final RectF srcDisplay, final RectF dstDisplay,
                            final RectF srcClip, final RectF dstClip,
                            final int srcColor, final int dstColor,
                            final float fromValue, final float toValue,
                            long duration, Animator.AnimatorListener listener) {
        if (srcDisplay == null || srcClip == null) return;

        final RectF srcRect = new RectF(0, 0,
                getDrawable().getIntrinsicWidth(),
                getDrawable().getIntrinsicHeight());

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setValues(
                PropertyValuesHolder.ofFloat("value", fromValue, toValue),

                PropertyValuesHolder.ofFloat("clipLeft", srcClip.left, dstClip.left),
                PropertyValuesHolder.ofFloat("clipTop", srcClip.top, dstClip.top),
                PropertyValuesHolder.ofFloat("clipRight", srcClip.right, dstClip.right),
                PropertyValuesHolder.ofFloat("clipBottom", srcClip.bottom, dstClip.bottom),

                PropertyValuesHolder.ofFloat("dstLeft", srcDisplay.left, dstDisplay.left),
                PropertyValuesHolder.ofFloat("dstTop", srcDisplay.top, dstDisplay.top),
                PropertyValuesHolder.ofFloat("dstRight", srcDisplay.right, dstDisplay.right),
                PropertyValuesHolder.ofFloat("dstBottom", srcDisplay.bottom, dstDisplay.bottom));

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final RectF dstRect = new RectF();

            public void onAnimationUpdate(ValueAnimator animation) {
                mClipRect = new RectF(
                        (float) animation.getAnimatedValue("clipLeft"),
                        (float) animation.getAnimatedValue("clipTop"),
                        (float) animation.getAnimatedValue("clipRight"),
                        (float) animation.getAnimatedValue("clipBottom"));

                dstRect.set((float) animation.getAnimatedValue("dstLeft"),
                        (float) animation.getAnimatedValue("dstTop"),
                        (float) animation.getAnimatedValue("dstRight"),
                        (float) animation.getAnimatedValue("dstBottom"));

                mBaseMatrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER);
                setImageMatrix(mBaseMatrix);

                float fraction = animation.getAnimatedFraction();
                int color = (int) mArgbEvaluator.evaluate(fraction, srcColor, dstColor);
                setBackgroundColor(color);

                if (mAnimatorUpdateListener != null) {
                    mAnimatorUpdateListener.onAnimationUpdate(
                            (float) animation.getAnimatedValue("value"));
                }
            }
        });
        valueAnimator.addListener(new SimpleAnimatorListener(listener));
        valueAnimator.start();
    }

    protected RectF getDisplayRect() {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            mDisplayRect.set(0, 0,
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            getDrawMatrix().mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    protected Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mApplyMatrix);
        return mDrawMatrix;
    }

    public void fadeOutAnim() {
        fadeOutAnim(1);
    }

    public void fadeOutAnim(final float scale) {
        int srcColor = (int) mArgbEvaluator.evaluate(scale
                , 0x00000000, 0xff000000);
        float duration = Math.min(scale, 1) * mFadeDuration;
        RectF displayRect = getDisplayRect();
        animator(displayRect, mFitThumbRect,
                displayRect, mThumbRect,
                srcColor, 0x00000000,
                1, 0, (long) duration,
                new SimpleAnimatorListener() {
                    public void onAnimationEnd(Animator animation, boolean isReverse) {
                        CommonKit.canForActivity(getContext()).finish();
                    }
                });
    }

    public void fadeInAnim() {
        animator(mFitThumbRect, mBaseRect,
                mThumbRect, mBaseRect,
                0x00000000, 0xff000000,
                0, 1,
                mFadeDuration, null);
    }

    public void setEnableFade(boolean enable) {
        mEnableAnim = enable;
        if (!mEnableAnim) {
            setBackgroundColor(0xff000000);
        }
    }
}
