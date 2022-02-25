/*
    The Android Not Open Source Project
    Copyright (c) 2014-9-6 wangzheng <iswangzheng@gmail.com>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    @author wangzheng  DateTime 2014-9-6
 */

package com.example.wangzheng.widget.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wangzheng.common.BitmapKit;
import com.example.wangzheng.common.CommonKit;

/**
 * Create by wangzheng on 2019/1/16
 */
public final class ImageSpan extends ReplacementSpan {
    private float scale;
    private float offsetY;

    private int leftMargin, rightMargin;
    private float ratio = 1.0f;
    private float radius = 0f;

    private Drawable mDrawable;
    private Paint mPaint;

    public ImageSpan(Context context, int resourceId) {
        Drawable drawable = context.getResources()
                .getDrawable(resourceId);
        applyBounds(drawable);
    }

    public ImageSpan(TextView tv, String url) {
        final Context context = tv.getContext();
        Glide.with(context).load(url).into(
                new ImageSpanTarget(tv) {
            public void handle(Drawable drawable) {
                applyBounds(drawable);
            }
        });
    }


    private void applyBounds(Drawable drawable) {
        if (drawable == null) return;
        this.mDrawable = drawable;
        mDrawable.setBounds(0, 0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
    }

    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end, Paint.FontMetricsInt fm) {
        if (mDrawable == null) return 0;
        final int dwidth = mDrawable.getIntrinsicWidth();
        final int dheight = mDrawable.getIntrinsicHeight();

        Rect rect = new Rect();
        paint.getTextBounds(text.toString(), start, end, rect);

        offsetY = rect.top * (0.5f + ratio / 2);
        scale = rect.height() * 1f / dheight * ratio;

        return leftMargin + rightMargin
                + (int) (dwidth * scale);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        if (mDrawable == null) return;
        int saveCount = canvas.save();
        canvas.translate(x + leftMargin, y + offsetY);
        canvas.scale(scale, scale);
        if (radius == 0) {
            mDrawable.draw(canvas);
        } else {
            updateBitmapShader();
            drawRoundDrawable(canvas);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawRoundDrawable(Canvas canvas) {
        if (mPaint == null) return;
        float r = radius / scale;
        RectF rect = new RectF(
                mDrawable.getBounds());
        canvas.drawRoundRect(rect, r, r, mPaint);
    }

    private void updateBitmapShader() {
        if (mPaint != null) return;
        Rect bounds = mDrawable.getBounds();
        Bitmap bitmap = BitmapKit.drawableToBitmap(
                mDrawable, bounds.width(), bounds.height());
        Shader shader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(shader);
    }

    public ImageSpan radius(float radius) {
        this.radius = CommonKit.dip2px(radius);
        return this;
    }

    public ImageSpan ratio(float ratio) {
        this.ratio = ratio;
        return this;
    }

    public ImageSpan margin(int leftMargin,
                            int rightMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        return this;
    }
}
