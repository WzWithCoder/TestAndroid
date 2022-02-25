package com.example.wangzheng.widget.viewpager;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.wangzheng.adapter.LoopPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wangzheng on 4/21/21
 */
public class ChangePageWrapper implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private List<ViewPager.OnPageChangeListener> mOnPageChangeListeners;

    public ChangePageWrapper(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof LoopPagerAdapter) {
            LoopPagerAdapter lpa = (LoopPagerAdapter) adapter;
            position = lpa.swapIndex(position);
        }
        for (ViewPager.OnPageChangeListener onPageChangeListener : mOnPageChangeListeners) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof LoopPagerAdapter) {
            LoopPagerAdapter lpa = (LoopPagerAdapter) adapter;
            position = lpa.swapIndex(position);
        }
        for (ViewPager.OnPageChangeListener onPageChangeListener : mOnPageChangeListeners) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        for (ViewPager.OnPageChangeListener onPageChangeListener : mOnPageChangeListeners) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
