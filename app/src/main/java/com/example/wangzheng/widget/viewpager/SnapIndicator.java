package com.example.wangzheng.widget.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.wangzheng.R;
import com.example.wangzheng.adapter.LoopPagerAdapter;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.widget.round.RoundImageView;
import com.example.wangzheng.widget.round.RoundLinearLayout;

/**
 * Create by wangzheng on 4/28/21
 */
public class SnapIndicator extends RoundLinearLayout implements
        ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    private ViewPager viewPager;
    private int size = 4;
    private int widthOffset = 7;
    private int space = 4;
    private int color = 0x66FFFFFF;

    public SnapIndicator(Context context) {
        this(context, null);
    }

    public SnapIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapIndicator(Context context
            , @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.SnapIndicator);
        color = attributes.getColor(
                R.styleable.SnapIndicator_color, color);
        size = attributes.getDimensionPixelSize(
                R.styleable.SnapIndicator_size, 4);
        space = attributes.getDimensionPixelSize(
                R.styleable.SnapIndicator_space, 4);
        widthOffset = attributes.getDimensionPixelSize(
                R.styleable.SnapIndicator_widthOffset, 7);

        attributes.recycle();

    }

    public void setupViewpager(ViewPager viewPager) {
        this.viewPager = viewPager;
        listenViewPageChange();

        inflateIndicator();
    }

    public final void inflateIndicator() {
        removeAllViews();
        final int count = obtainPageCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            view = createIndicator(i);
            addView(view);
        }
        post(()-> setDefaultIndex());
    }

    private final View createIndicator(int index) {
        Context context = getContext();
        RoundImageView indicator =
                new RoundImageView(context);
        indicator.setBackgroundColor(color);
        indicator.setShape(2);

        LayoutParams layoutParams =
                new LayoutParams(size, size);
        layoutParams.leftMargin = space;
        if (index == 0) {
            layoutParams.leftMargin = 0;
        }
        indicator.setLayoutParams(layoutParams);
        return indicator;
    }

    public final void setDefaultIndex() {
        int index = viewPager.getCurrentItem();
        float positionOffset = 0;
        int positionOffsetPixels = 1;
        onPageScrolled(index, positionOffset,
                positionOffsetPixels);
    }

    @Override
    public void onPageScrolled(int position
            , float ratio, int positionOffsetPixels) {
        int leftIndex  = swapIndex(position);
        int rightIndex = swapIndex(position + 1);
        if (rightIndex >= getChildCount()) return;

        View rightView = getChildAt(rightIndex);
        updateViewStatus(rightView, ratio);

        View leftView = getChildAt(leftIndex);
        ratio = 1 - ratio;
        updateViewStatus(leftView, ratio);
    }

    private void updateViewStatus(View view, float ratio) {
        LayoutParams lp = (LayoutParams)
                view.getLayoutParams();
        lp.width = (int) (size + ratio * widthOffset);
        view.setLayoutParams(lp);

        int color = caculateColor(ratio);
        view.setBackgroundColor(color);
    }

    private final int caculateColor(float ratio) {
        final int endAlpha = 0xBB;
        int startAlpha = CommonKit.makeAlphaWithColor(color);
        int offsetAlpha = endAlpha - startAlpha;
        float alpha = startAlpha + offsetAlpha * ratio;
        return ColorUtils.setAlphaComponent(
                color, (int) alpha);
    }

    protected final void listenViewPageChange() {
        if (viewPager instanceof AutoScrollViewPager) {
            AutoScrollViewPager asvp = null;
            asvp = (AutoScrollViewPager) viewPager;
            asvp.addRealPageChangeListener(this);
        } else {
            viewPager.addOnPageChangeListener(this);
        }
        viewPager.addOnAdapterChangeListener(this);
        PagerAdapter adapter = viewPager.getAdapter();
        adapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                inflateIndicator();
            }
        });
    }

    public final int obtainPageCount() {
        PagerAdapter adapter = viewPager.getAdapter();
        int count = adapter.getCount();
        if (adapter instanceof LoopPagerAdapter) {
            count = count - 2;
        }
        return count;
    }

    private final int swapIndex(int index) {
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof LoopPagerAdapter) {
            LoopPagerAdapter lpa = null;
            lpa = (LoopPagerAdapter) adapter;
            index = swapIndex(lpa, index);
        }
        return index;
    }

    public final int swapIndex(LoopPagerAdapter adapter, int position) {
        int count = adapter.getCount();
        int realCount = adapter.getRealCount();
        if (realCount < 2) {
            position = position;
        } else if (position == count - 1) {
            position = 0;
        } else if (position == 0) {
            position = realCount - 1;
        } else {
            --position;
        }
        return position;
    }

    @Override
    public void onAdapterChanged(ViewPager viewPager
            , @NonNull PagerAdapter oldAdapter
            , @Nullable PagerAdapter newAdapter) {
        inflateIndicator();
    }

    @Override
    public void onPageSelected(int position) {
        //wo can do nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //wo can do nothing
    }
}
