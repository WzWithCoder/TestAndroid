package com.example.wangzheng.widget.tv_fold;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextDirectionHeuristic;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.wangzheng.R;
import com.example.wangzheng.common.Callable;
import com.example.wangzheng.widget.span.LinkMovementDelegate;
import com.example.wangzheng.widget.span.FakeBoldSpan;
import com.example.wangzheng.widget.span.SpanBuilder;


/**
 * Create by wangzheng on 12/9/20
 */
public class FoldTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int maxLines = -1;
    private CharSequence ellipsize;
    private int ellipsizeColor;
    private CharSequence action;
    private int actionColor;

    private boolean canFoldText = true;
    private boolean isExpended = true;
    private CharSequence mContent;
    private CharSequence mSuffixText;

    private Callable<Object, Boolean> mActionHandle;

    public FoldTextView(Context context) {
        this(context, null);
    }

    public FoldTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoldTextView(Context context
            , @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.FoldTextView);

        ellipsize = attributes.getText(R.styleable.FoldTextView_ellipsize_label);
        ellipsizeColor = attributes.getColor(
                R.styleable.FoldTextView_ellipsize_color, Color.BLUE);

        action = attributes.getText(R.styleable.FoldTextView_action_label);
        actionColor = attributes.getColor(
                R.styleable.FoldTextView_action_color, Color.BLUE);

        attributes.recycle();

        maxLines = getMaxLines();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final TextView view = this;
        if (LinkMovementDelegate.onTouchEvent(
                view, event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (isExpended) {
            mContent = text;
        }
        post(()-> foldText(type));
    }

    private final void foldText(BufferType type) {
        if (mContent == null) return;
        CharSequence temp = mContent;
        if (canFoldText) {
            temp = caculateText(mContent);
        }
        super.setText(temp, type);
    }

    public void update(CharSequence text, boolean canFoldText) {
        super.setText(text, BufferType.SPANNABLE);
        this.canFoldText = canFoldText;

        int lines = Integer.MAX_VALUE;
        if (canFoldText) {
            lines = maxLines;
        }
        super.setMaxLines(lines);
    }

    public void setSuffix(CharSequence suffix) {
        this.mSuffixText = suffix;
    }

    public void actionHandle(Callable<Object, Boolean> callable) {
        this.mActionHandle = callable;
    }

    public final void toggle() {
        foldAnimator(!canFoldText);
    }

    public final void foldAnimator(boolean canFoldText) {
        CharSequence text = mContent;
        if (canFoldText) {
            text = caculateText(mContent);
        }
        Layout layout = makeSingleLayout(text);
        final int end = layout.getHeight()
                + getPaddingTop()
                + getPaddingBottom();
        final int start = getHeight();

        CollapseAnimator.with(this).of(start, end)
                .listener(new FoldListener(
                        this,
                        canFoldText,
                        text))
                .startAnimator();
    }

    public CharSequence caculateText(CharSequence text) {
        CharSequence suffix = combineTextSuffix();

        Layout layout = makeSingleLayout(text);
        int lineCount = layout.getLineCount();
        int maxLineIndex = maxLines - 1;
        int maxIndex = layout.getLineEnd(maxLineIndex);
        CharSequence tempText = null;
        while (lineCount > maxLines && maxIndex > 0) {
            tempText = text.subSequence(0, maxIndex--);
            layout = makeSingleLayout(SpanBuilder
                    .from(tempText)
                    .append(suffix));
            lineCount = layout.getLineCount();
        }
        if (isExpended = tempText == null) {
            return text;
        } else {
            return SpanBuilder.from(tempText).append(suffix);
        }
    }

    public Layout makeSingleLayout(CharSequence text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return makeLayoutBaseImpl(text);
        } else {
            return makeLayoutApi28Impl(text);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.BASE)
    private Layout makeLayoutBaseImpl(CharSequence text) {
        final int wantWidth = getWidth()
                - getPaddingLeft()
                - getPaddingRight();
        DynamicLayout layout = new DynamicLayout(
                text, getPaint(), wantWidth,
                Layout.Alignment.ALIGN_NORMAL,
                getLineSpacingMultiplier(),
                getLineSpacingExtra(),
                getIncludeFontPadding());
        return layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private Layout makeLayoutApi28Impl(CharSequence text) {
        final int wantWidth = getWidth()
                - getPaddingLeft()
                - getPaddingRight();
        DynamicLayout.Builder builder = DynamicLayout.Builder
                .obtain(text, getPaint(), wantWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(getLineSpacingExtra(),
                        getLineSpacingMultiplier())
                .setIncludePad(getIncludeFontPadding())
                .setUseLineSpacingFromFallbacks(
                        isFallbackLineSpacing())
                .setBreakStrategy(getBreakStrategy())
                .setHyphenationFrequency(getHyphenationFrequency())
                .setJustificationMode(getJustificationMode());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TextDirectionHeuristic textDir = null;
            textDir = getTextDirectionHeuristic();
            builder.setTextDirection(textDir);
        }
        return builder.build();
    }

    public final CharSequence combineTextSuffix() {
        if (!TextUtils.isEmpty(mSuffixText)) {
            return mSuffixText;
        }
        SpanBuilder textSuffix = new SpanBuilder();
        if (!TextUtils.isEmpty(ellipsize)) {
            Object color = new ForegroundColorSpan(
                    ellipsizeColor);
            textSuffix.append(ellipsize, color);
        }
        if (!TextUtils.isEmpty(action)) {
            Object listener = new ActionListener();
            Object fake = new FakeBoldSpan();
            Object color = new ForegroundColorSpan(
                    actionColor);
            textSuffix.append(action,
                    color,
                    fake,
                    listener);
        }
        return textSuffix;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private class ActionListener extends ClickableSpan {
        public void onClick(@NonNull View widget) {
            if (mActionHandle == null) return;
            if (mActionHandle.call(widget)) {
                foldAnimator(false);
            }
        }
    }
}
