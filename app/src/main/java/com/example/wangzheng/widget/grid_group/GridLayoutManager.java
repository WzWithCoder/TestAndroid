package com.example.wangzheng.widget.grid_group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.wangzheng.R;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Create by wangzheng on 2019/7/2
 */
public class GridLayoutManager {
    private float vSpacing;
    private float hSpacing;
    private int   numColumns = 3;
    private int   dividerColor;
    private float dividerSize;

    private ViewGroup anchor;

    GridLayoutManager(ViewGroup anchor, AttributeSet attrs) {
        this.anchor = anchor;
        Context context = anchor.getContext();
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.GridGroup);

        numColumns = attributes.getInt(R.styleable
                .GridGroup_numColumns, numColumns);
        hSpacing = attributes.getDimension(R.styleable
                .GridGroup_horizontalSpacing, 0);
        vSpacing = attributes.getDimension(R.styleable
                .GridGroup_verticalSpacing, 0);
        dividerSize = attributes.getDimension(R.styleable
                .GridGroup_dividerSize, 1);
        dividerColor = attributes.getColor(R.styleable
                .GridGroup_dividerColor, 0);

        attributes.recycle();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int width = r - l - getPaddingRight() - getPaddingLeft();
        int columnWidth = (int) (width - hSpacing
                * (numColumns - 1)) / numColumns;
        int index = -1;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                continue;
            }
            ++index;
            int rowIndex = index2Row(index);
            int columnIndex = index2Column(index);

            int x = getPaddingLeft() + columnIndex * columnWidth
                    + (int) (columnIndex * hSpacing);
            int y = getPaddingTop() + getRowTop(rowIndex)
                    + (int) (rowIndex * vSpacing);
            int height = view.getMeasuredHeight();
            view.layout(x, y, x + columnWidth, y + height);
        }
    }

    private Map<Integer, Integer> mRowHeight = new HashMap<>();

    protected int[] onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mRowHeight.clear();
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int contentWidth = width - getPaddingRight() - getPaddingLeft();
        float columnWidth = (contentWidth - hSpacing
                * (numColumns - 1)) / numColumns;
        int columnWidthSpec = View.MeasureSpec.makeMeasureSpec(
                (int) columnWidth, View.MeasureSpec.EXACTLY);
        int childCount = getChildCount();
        View child = null;
        int index = -1;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            child.measure(columnWidthSpec, View.MeasureSpec.UNSPECIFIED);
            if (child.getVisibility() == GONE) {
                continue;
            }
            ++index;
            int height = child.getMeasuredHeight();
            int rowIndex = index2Row(index);
            Integer rowHeight = mRowHeight.get(rowIndex);
            if (rowHeight == null || rowHeight < height) {
                mRowHeight.put(rowIndex, height);
            }
        }
        int rowsHeight = getRowsHeight();
        int rows = getRows(getGridCount());
        float height = rowsHeight + (rows - 1) * vSpacing
                + getPaddingTop() + getPaddingBottom();
        return new int[]{width, (int) height};
    }


    protected void onDraw(Canvas canvas) {
        //分割线
    }

    public int getGridCount() {
        int count = 0;
        View child = null;
        for (int i = 0; i < getChildCount(); i++) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            ++count;
        }
        return count;
    }


    public int getRowTop(int row) {
        int rowHeight = 0;
        for (int i = 0; i < row; i++) {
            rowHeight += mRowHeight.get(i);
        }
        return rowHeight;
    }

    public int getRowsHeight() {
        int rowHeight = 0;
        for (Integer height : mRowHeight.values()) {
            rowHeight += height;
        }
        return rowHeight;
    }

    public int index2Column(int index) {
        return index % numColumns;
    }

    public int index2Row(int index) {
        int count = index + 1;
        int rows = getRows(count);
        return rows - 1;
    }

    public int getRows(int count) {
        int rows = count / numColumns;
        if (count % numColumns > 0) {
            rows += 1;
        }
        return rows;
    }

    private int getMeasuredWidth() {
        return anchor.getMeasuredWidth();
    }

    private int getChildCount() {
        return anchor.getChildCount();
    }

    private int getPaddingRight() {
        return anchor.getPaddingRight();
    }

    private int getPaddingLeft() {
        return anchor.getPaddingLeft();
    }

    private int getPaddingBottom() {
        return anchor.getPaddingBottom();
    }

    private int getPaddingTop() {
        return anchor.getPaddingTop();
    }

    private View getChildAt(int i) {
        return anchor.getChildAt(i);
    }
}
