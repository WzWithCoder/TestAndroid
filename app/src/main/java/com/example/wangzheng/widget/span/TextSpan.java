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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.util.Log;

public class TextSpan extends ReplacementSpan implements ParagraphStyle {

    private int borderWidth, borderHeight;
    private float ratio = -1f;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private int verticalPadding, horizontalPadding,
            leftMargin, rightMargin,
            corner;

    public TextSpan() {
        this.corner = 2;
        this.ratio = 0.8f;
        this.horizontalPadding = 2;
        this.verticalPadding = 1;

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(1f);
        mBgPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }

    public static TextSpan builder() {
        return new TextSpan();
    }

    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end, Paint.FontMetricsInt fm) {
        mTextPaint.setTextSize(paint.getTextSize() * ratio);
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text.toString(), start, end, rect);
        borderWidth = rect.width() + horizontalPadding * 2;
        borderHeight = rect.height() + verticalPadding * 2;
        float strokeWidth = mBgPaint.getStrokeWidth();
        return borderWidth + leftMargin + rightMargin + (int)strokeWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Rect srcRect = new Rect();
        paint.getTextBounds(text.toString(), start, end, srcRect);
        float strokeWidth = mBgPaint.getStrokeWidth() / 2f;
        srcRect.offset((int) (x), y);
        //绘制背景
        RectF roundRect = new RectF(0f, 0f, borderWidth, borderHeight);
        float offsetY = (srcRect.height() - borderHeight) / 2;
        roundRect.offset(x + strokeWidth + leftMargin, srcRect.top + offsetY);
        canvas.drawRoundRect(roundRect, corner, corner, mBgPaint);
        //绘制文字
        float baseLine = (roundRect.bottom + roundRect.top -
                mTextPaint.descent() - mTextPaint.ascent()) / 2;
        canvas.drawText(text, start, end, roundRect.centerX(), baseLine, mTextPaint);
    }

    public TextSpan setStyle(Paint.Style style) {
        mBgPaint.setStyle(style);
        return this;
    }

    public TextSpan setBackgroundColor(int backgroundColor) {
        mBgPaint.setColor(backgroundColor);
        return this;
    }

    public TextSpan setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        return this;
    }

    public TextSpan setPadding(int horizontalPadding, int verticalPadding) {
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        return this;
    }

    public TextSpan setStrokeWidth(float strokeWidth) {
        mBgPaint.setStrokeWidth(strokeWidth);
        return this;
    }

    public TextSpan setCorner(int corner) {
        this.corner = corner;
        return this;
    }

    public TextSpan setRatio(float ratio) {
        this.ratio = ratio;
        return this;
    }

    public TextSpan setMargin(int leftMargin, int rightMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        return this;
    }
}
