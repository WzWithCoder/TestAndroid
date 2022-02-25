package com.example.wangzheng.widget.zoom_imageview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.DecelerateInterpolator;

import com.example.wangzheng.common.SimpleDoubleTapListener;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;


/**
 * Create by wangzheng on 2018/7/30
 */
public class ZoomImageView extends FadeImageView {
    private final static String TAG = ZoomImageView.class.getSimpleName();

    private GestureDetector      mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private FlingRunnable mFlingRunnable;

    private final float[] mMatrixValues = new float[9];

    private boolean mSlide;
    private boolean mScale;
    private int mScrollEdge = EDGE_BOTH;

    private static final int EDGE_NONE  = -1;
    private static final int EDGE_LEFT  = 0;
    private static final int EDGE_RIGHT = 1;
    private static final int EDGE_BOTH  = 2;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mFlingRunnable = new FlingRunnable(this) {
            public void apply(float dx, float dy) {
                mApplyMatrix.postTranslate(dx, dy);
                setImageMatrix(getDrawMatrix());
                awakenScrollBars();
            }
        };

        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public boolean onScale(ScaleGestureDetector detector) {
                        mScale = true;
                        float scaleFactor = detector.getScaleFactor();
                        if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                            return false;
                        scale(scaleFactor, detector.getFocusX(), detector.getFocusY());
                        return true;
                    }
                });

        mGestureDetector = new android.view.GestureDetector(getContext(),
                new android.view.GestureDetector.SimpleOnGestureListener() {
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                        if (mScale || e1.getPointerCount() > 1
                                || e2.getPointerCount() > 1) return false;
                        if (computeHorizontalScrollRange() > computeHorizontalScrollExtent() ||
                                computeVerticalScrollRange() > computeVerticalScrollExtent()) {
                            scroll(-dx, -dy);
                        } else if (mSlide || Math.abs(e2.getY() - e1.getY()) >
                                Math.abs(e2.getX() - e1.getX())) {
                            slide(e1, e2, dx, dy);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        return true;
                    }

                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        fling(e2.getX(), e2.getY(), -velocityX, -velocityY);
                        return true;
                    }
                });

        mGestureDetector.setOnDoubleTapListener(new SimpleDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                float scale = getScale();
                float focusX = e.getX();
                float focusY = e.getY();
                if (scale > 1) {
                    scaleAnimator(scale, 1f, focusX, focusY);
                } else {
                    scaleAnimator(scale, scale * 2f, focusX, focusY);
                }
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                fadeOutAnim();
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && null != getDrawable() && isShown()) {
            if (getScale() > 1.3) {
                scaleAnimator(getScale(), 1,
                        mBaseRect.centerX(), mBaseRect.centerY());
            } else {
                fadeOutAnim();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        float scale = getScale();
        RectF rect = getDisplayRect();
        if (!isFocused() && scale > 1 && rect != null) {
            mApplyMatrix.setScale(1f, 1f,
                    rect.centerX(), rect.centerY());
            if (checkMatrixBounds()) {
                setImageMatrix(getDrawMatrix());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getDrawable() == null && getWidth() *
                getHeight() == 0) return false;

        boolean handle = false;
        switch (event.getAction()) {
            case ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mFlingRunnable.cancelFling();
                break;
            case ACTION_CANCEL:
            case ACTION_UP:
                if (mSlide && getScale() < 0.8f) {
                    fadeOutAnim(getScale());
                } else if (getScale() < 1f) {
                    setBackgroundColor(0xff000000);
                    scaleAnimator(getScale(), 1,
                            mBaseRect.centerX(), mBaseRect.centerY());
                }
                mSlide = false;
                mScale = false;
                break;
        }
        if (!mSlide && null != mScaleGestureDetector) {
            handle = mScaleGestureDetector.onTouchEvent(event);
        }
        if (!mScale && null != mGestureDetector
                && mGestureDetector.onTouchEvent(event)) {
            handle = true;
        }
        return super.onTouchEvent(event) || handle;
    }

    public void scroll(float dx, float dy) {
        mApplyMatrix.postTranslate(dx, dy);
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
        awakenScrollBars();
        if ((mScrollEdge == EDGE_LEFT && dx >= 1f)
                || (mScrollEdge == EDGE_RIGHT && dx <= -1f)
                || mScrollEdge == EDGE_BOTH) {
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    public void slide(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mSlide = true;

        float scaleFactor = 1f + distanceY / getHeight();
        if (e2.getY() < e1.getY()) {
            scaleFactor = 1f - distanceY / getHeight();
        }
        mApplyMatrix.postScale(scaleFactor, scaleFactor, e2.getX(), e2.getY());
        mApplyMatrix.postTranslate(-distanceX, -distanceY);
        setImageMatrix(getDrawMatrix());

        float scale = getScale();
        if (scale <= 1) {
            int color = (int) mArgbEvaluator.evaluate(scale
                    , 0x00000000, 0xff000000);
            setBackgroundColor(color);
            if (mAnimatorUpdateListener != null) {
                mAnimatorUpdateListener.onAnimationUpdate(scale);
            }
        }
    }

    public void fling(float startX, float startY, float velocityX, float velocityY) {
        mFlingRunnable.cancelFling();
        mFlingRunnable.fling(getWidth(), getHeight(), (int) velocityX, (int) velocityY, getDisplayRect());
    }

    public void scale(float scaleFactor, float focusX, float focusY) {
        mApplyMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    public void rotate(float degrees) {
        mApplyMatrix.postRotate(degrees % 360);
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    private void scaleAnimator(float fromScale, float toScale,
                               final float focusX, final float focusY) {
        ValueAnimator valueAnimator = ValueAnimator
                .ofFloat(fromScale, toScale)
                .setDuration(200);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float scaleFactor = (float) animation.getAnimatedValue();
                mApplyMatrix.setScale(scaleFactor, scaleFactor, focusX, focusY);
                if (checkMatrixBounds()) {
                    setImageMatrix(getDrawMatrix());
                }
            }
        });
        valueAnimator.start();
    }

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect();
        if (null == rect) {
            return false;
        }

        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;

        final int viewHeight = getHeight();
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        final int viewWidth = getWidth();
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
            mScrollEdge = EDGE_BOTH;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
            mScrollEdge = EDGE_LEFT;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
            mScrollEdge = EDGE_RIGHT;
        } else {
            mScrollEdge = EDGE_NONE;
        }
        // Finally actually translate the matrix
        mApplyMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mApplyMatrix, Matrix.MSCALE_X), 2)
                + (float) Math.pow(getValue(mApplyMatrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private RectF getScrollRange() {
        RectF rect = getDisplayRect();
        if (rect == null) {
            rect = mBaseRect;
        }
        return rect;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return (int) getScrollRange().width();
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return -(int) getScrollRange().left;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return (int) getScrollRange().height();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return -(int) getScrollRange().top;
    }
}
