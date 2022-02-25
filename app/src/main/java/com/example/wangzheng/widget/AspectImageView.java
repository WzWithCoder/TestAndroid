package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.example.wangzheng.widget.round.RoundImageView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 以View某一边为基准，按图片比例等比缩放View
 * 建议使用 ScaleType.FIT_XY
 * Create by wangzheng on 2019/6/28
 */
public class AspectImageView extends RoundImageView {
    public AspectImageView(Context context) {
        this(context, null);
    }

    public AspectImageView(Context context,
                           AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AspectImageView(Context context,
                           AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reszieIfNeeded();
    }

    private void reszieIfNeeded() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            return;
        }

        Drawable drawable = getDrawable();
        if (drawable == null) {
            drawable = getBackground();
        }
        if (drawable == null) {
            return;
        }

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();

        int vWidth = getMeasuredWidth();
        int vHeight = getMeasuredHeight();

        float scale = 0f;
        int lwidth = layoutParams.width;
        int lheight = layoutParams.height;

        if (lheight > 0 || lheight == MATCH_PARENT) {
            scale = dWidth * 1f / dHeight;
            vWidth = (int) (vHeight * scale);
        } else if (lwidth > 0 || lwidth == MATCH_PARENT) {
            scale = dHeight * 1f / dWidth;
            vHeight = (int) (vWidth * scale);
        }

        if (vWidth > 0 && vHeight > 0 && scale > 0) {
            setMeasuredDimension(vWidth, vHeight);
        }
    }
}