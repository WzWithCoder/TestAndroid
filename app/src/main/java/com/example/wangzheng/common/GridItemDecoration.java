package com.example.wangzheng.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qibin on 2015/11/7.
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    public GridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public GridItemDecoration(Context context, boolean header) {
        this(context);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);

    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isFullSpan(parent, view)) {
            outRect.bottom = 20;
            return;
        }
        if (!isLastColumn(parent, view)) {
            outRect.right = 20;
        }
        if (!isLastRaw(parent, view)) {
            outRect.bottom = 20;
        }
        Log.e("xxx", position + outRect.toString());
    }

    private boolean isLastRaw(RecyclerView parent, View view) {
        int childCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        int spanCount = getSpanCount(parent);
        boolean maybeLastRaw = childCount - position - 1 <= spanCount;
        if (!maybeLastRaw) return false;
        if (childCount - 1 == position) return true;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int lastPosition = -1;

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup spanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            return spanSizeLookup.getSpanGroupIndex(position, spanCount) ==
                    spanSizeLookup.getSpanGroupIndex(childCount - 1, spanCount);
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastPosition = childCount - 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            if(isFullSpan(parent,view))return false;
            else return true;
        }
        return lastPosition == position;
    }


    private boolean isLastColumn(RecyclerView parent, View view) {
        if (isFullSpan(parent, view)) return true;
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup spanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            int spanCount = getSpanCount(parent);
            if (position + 1 == parent.getAdapter().getItemCount()) {
                return true;
            }
            return spanSizeLookup.getSpanGroupIndex(position, spanCount) !=
                    spanSizeLookup.getSpanGroupIndex(position + 1, spanCount);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            return getSpanCount(parent) - 1 == layoutParams.getSpanIndex();
        }
        return false;
    }


    private boolean isFullSpan(RecyclerView parent, View view) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams layoutParams =
                    (GridLayoutManager.LayoutParams) view.getLayoutParams();
            return getSpanCount(parent) == layoutParams.getSpanSize();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            return layoutParams.isFullSpan();
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }
}
