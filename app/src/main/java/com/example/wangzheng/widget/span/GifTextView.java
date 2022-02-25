package com.example.wangzheng.widget.span;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.wangzheng.common.Reflector;
import com.example.wangzheng.widget.tv_fold.FoldTextView;

public class GifTextView extends FoldTextView {
    public GifTextView(Context context) {
        super(context);
    }

    public GifTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GifTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void invalidate(Rect dirty) {
        super.invalidate(dirty);
    }

    public void invalidateRegion(int start, int end) {
        //invalidateRegion(start,end,true);
        try {
            invokeInvalidateRegion(start,end);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void invokeInvalidateRegion(int start, int end) throws Exception{
        String methodName = "invalidateRegion";
        Reflector.with(this).method(
                methodName,
                Integer.class,
                Integer.class,
                Boolean.class
        ).call(start, end, true);
    }

    //    @Override
//    public void invalidateDrawable(@NonNull Drawable drawable) {
//        if (drawable instanceof IDrawable) {
//            Rect bounds = ((IDrawable) drawable).getDirtyBounds();
//            invalidate(bounds.left, bounds.top,
//                    bounds.right, bounds.bottom);
//        } else {
//            super.invalidateDrawable(drawable);
//        }
//    }
}
