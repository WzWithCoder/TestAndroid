package com.example.wangzheng.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.example.wangzheng.R;

import java.lang.reflect.Field;
import java.util.Arrays;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class CellEditText extends AppCompatEditText {
    private int   mMaxLength = 8;
    private int   mFocusColor;
    private int   mBorderColor;
    private int   mBorderSize;
    private int   mBorderRadius;
    private Paint mBordePaint;
    private Paint mTextPaint;

    private float[] mRadiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    public CellEditText(Context context) {
        this(context, null);
    }

    public CellEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources.Theme theme = context.getTheme();
        TypedArray typedArray = theme.obtainStyledAttributes(attrs,
                R.styleable.CellEditText, defStyleAttr, 0);
        mFocusColor = typedArray.getColor(R.styleable.CellEditText_focusColor, Color.BLUE);
        mBorderColor = typedArray.getColor(R.styleable.CellEditText_borderColor, Color.BLACK);
        mBorderSize = typedArray.getDimensionPixelSize(
                R.styleable.CellEditText_borderSize, 1);
        mBorderRadius = typedArray.getDimensionPixelSize(
                R.styleable.CellEditText_borderRadius, 15);
        typedArray.recycle();

        try {
            Class clazz = Class.forName("com.android.internal.R$styleable");
            Field field = clazz.getDeclaredField("TextView");
            field.setAccessible(true);
            int[] textStyleAttr = (int[]) field.get(null);

            field = clazz.getDeclaredField("TextView_maxLength");
            field.setAccessible(true);
            int maxLengthStyle = (Integer) field.get(null);

            TypedArray textTypedArray = theme.obtainStyledAttributes(attrs,
                    textStyleAttr, defStyleAttr, 0);
            mMaxLength = textTypedArray.getInt(maxLengthStyle, 8);
            textTypedArray.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBackgroundColor(Color.TRANSPARENT);
        setCursorVisible(false);
        setFocusableInTouchMode(true);
        setSingleLine();

        mBordePaint = new Paint(ANTI_ALIAS_FLAG);
        mBordePaint.setStrokeWidth(mBorderSize);
        mBordePaint.setStyle(Paint.Style.STROKE);
        mBordePaint.setColor(mBorderColor);
        mBordePaint.setAntiAlias(true);

        mTextPaint = new TextPaint(getPaint());
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(getCurrentTextColor());
        LinearGradient linearGradient = new LinearGradient(
                0, 0, 100, 100,
                Color.RED, Color.YELLOW,
                LinearGradient.TileMode.MIRROR);
        mTextPaint.setShader(linearGradient);
    }

    @Override
    protected void onFocusChanged(boolean focused,
                                  int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int position = getText().length();
        drawBorder(canvas);
        if (position > 0) {
            drawPoint(canvas, position);
        }
        if (position < mMaxLength && isFocused()) {
            drawFocus(canvas, position);
        }
    }

    private int getRealWidth() {
        return getWidth() - mBorderSize * 2;
    }

    private void drawPoint(Canvas canvas, int length) {
        float cellWidth = getRealWidth() / mMaxLength;
        boolean wasPasswordType = isPasswordInputType(getInputType());

        String text = getText().toString();
        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(text, 0, 1, textBounds);

        float x = 0;
        for (int i = 0; i < length; i++) {
            x = mBorderSize + i * cellWidth + cellWidth / 2;
            if (wasPasswordType) {
                canvas.drawCircle(x, getHeight() / 2,
                        cellWidth / 10, mTextPaint);
            } else {
                canvas.drawText(String.valueOf(text.charAt(i)), x,
                        (getHeight() + textBounds.height()) / 2, mTextPaint);
            }
        }
    }

    private void drawBorder(Canvas canvas) {
        mBordePaint.setColor(mBorderColor);

        RectF rect = new RectF(mBorderSize, mBorderSize,
                getWidth() - mBorderSize, getHeight() - mBorderSize);
        canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, mBordePaint);

        float cellWidth = rect.width() / mMaxLength;
        for (int i = 1; i < mMaxLength; i++) {
            canvas.drawLine(i * cellWidth + rect.left, rect.top,
                    i * cellWidth + rect.left, rect.bottom,
                    mBordePaint);
        }
    }

    private void drawFocus(Canvas canvas, int position) {
        float cellWidth = getRealWidth() * 1f / mMaxLength;
        Arrays.fill(mRadiusArray, 0);
        if (position == 0) {
            mRadiusArray[0] = mRadiusArray[1] = mBorderRadius;
            mRadiusArray[6] = mRadiusArray[7] = mBorderRadius;
        } else if (position == mMaxLength - 1) {
            mRadiusArray[2] = mRadiusArray[3] = mBorderRadius;
            mRadiusArray[4] = mRadiusArray[5] = mBorderRadius;
        }
        RectF rect = new RectF(position * cellWidth + mBorderSize, mBorderSize,
                (position + 1) * cellWidth + mBorderSize, getHeight() - mBorderSize);
        Path path = new Path();
        path.addRoundRect(rect, mRadiusArray, Path.Direction.CW);
        mBordePaint.setColor(mFocusColor);
        canvas.drawPath(path, mBordePaint);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(getText().length());
    }

    @Override
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        super.setTextColor(color);
    }

    public void setFocusColor(int focusColor) {
        this.mFocusColor = focusColor;
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        invalidate();
    }

    public void setBorderSize(int borderSize) {
        this.mBorderSize = borderSize;
        invalidate();
    }

    static boolean isPasswordInputType(int inputType) {
        final int variation = inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        return variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
    }
}