package com.example.wangzheng.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据View的内容自动适应高度的ViewPager
 * Created by wangzheng on 2017/1/11.
 */
public class AutofitViewPager extends ViewPager implements ViewPager.OnAdapterChangeListener {

    private Map<Integer, Integer> mSizeTable = new HashMap<>();
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private int mDefaultHeight = -1;

    public AutofitViewPager(Context context) {
        this(context, null);
    }

    public AutofitViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnAdapterChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mWidthMeasureSpec = widthMeasureSpec;
        this.mHeightMeasureSpec = heightMeasureSpec;

        if (mDefaultHeight == -1) {
            heightMeasureSpec = makeMeasureSpec();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected final int makeMeasureSpec() {
        final int index = getCurrentItem();
        View child = findViewWithTag(index);
        if (child == null) {
            child = findDefaultView();
        }
        if (child == null) {
            return -1;
        }
        measureChild(child, mWidthMeasureSpec,
                mHeightMeasureSpec);
        mDefaultHeight = child.getMeasuredHeight();
        return MeasureSpec.makeMeasureSpec(
                mDefaultHeight,
                MeasureSpec.EXACTLY);
    }

    @Override
    public void onPageScrolled(int position
            , float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset,
                positionOffsetPixels);

        Integer leftHeight = fetchChildHeight(position);
        if (leftHeight == null) return;

        Integer rightHeight = fetchChildHeight(position + 1);
        if (rightHeight == null) return;

        float currHeight = rightHeight * positionOffset +
                leftHeight * (1 - positionOffset);

        //为ViewPager设置高度
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = (int) currHeight;
        setLayoutParams(params);
    }

    protected final int fetchChildHeight(int index) {
        if (mSizeTable.containsKey(index)) {
            return mSizeTable.get(index);
        }
        View child = findViewWithTag(index);
        if (child == null) {
            return mDefaultHeight;
        }
        measureChild(child, mWidthMeasureSpec, mHeightMeasureSpec);
        mDefaultHeight = child.getMeasuredHeight();
        mSizeTable.put(index, mDefaultHeight);
        return mDefaultHeight;
    }

    @Override
    public void onAdapterChanged(ViewPager viewPager
            , @Nullable PagerAdapter oldAdapter
            , @Nullable PagerAdapter newAdapter) {
        mSizeTable.clear();
    }

    protected final View findDefaultView() {
        View childView = null;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            if (isViewDecor(childView)) {
                childView = null;
            } else {
                break;
            }
        }
        return childView;
    }

    protected final boolean isViewDecor(View view) {
        LayoutParams layoutParams = (LayoutParams)
                view.getLayoutParams();
        return layoutParams != null &&
                layoutParams.isDecor;
    }
}

