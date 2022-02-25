package com.example.wangzheng.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wangzheng.common.CommonKit;


/**
 * RecyclerView 顶部渐变隐藏
 * Create by wangzheng on 2020/3/12
 */
public class TopShadow extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int mLayerId;
    private int mHeight;

    public TopShadow() {
        this(CommonKit.dip2px(30));
    }

    public TopShadow(int height) {
        this.mHeight = height;
        this.mPaint = new Paint();

        float x0 = 0, y0 = 0, x1 = 0;
        int[] colors = {0X0, Color.WHITE};
        float[] positions = {0f, 1.0f};
        LinearGradient lGradient = new LinearGradient(
                x0, y0, x1, height,
                colors, positions,
                Shader.TileMode.CLAMP);
        mPaint.setShader(lGradient);

        Xfermode xfermode = new PorterDuffXfermode(
                PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
    }

    @Override
    public void onDrawOver(Canvas canvas
            , RecyclerView parent, RecyclerView.State state) {
        if (isScrollTop(parent)) return;
        float left = 0, top = 0;
        int width = parent.getWidth();
        canvas.drawRect(left, top, width,
                mHeight, mPaint);
        canvas.restoreToCount(mLayerId);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent,
                       RecyclerView.State state) {
        if (isScrollTop(parent)) return;
        float left = 0, top = 0;
        float right = parent.getWidth();
        float bottom = parent.getHeight();
        final Paint paint = null;
        mLayerId = canvas.saveLayer(
                left, top,
                right, bottom,
                paint,//may be null
                Canvas.ALL_SAVE_FLAG);
    }

    public static boolean isScrollTop(View view) {
        //表示是否能向下滚动，false表示已经滚动到顶部
        return !view.canScrollVertically(-1);
    }

}
