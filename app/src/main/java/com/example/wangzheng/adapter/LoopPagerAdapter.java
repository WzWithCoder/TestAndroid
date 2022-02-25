package com.example.wangzheng.adapter;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;

public final class LoopPagerAdapter extends PagerAdapter {

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public static LoopPagerAdapter delegate(ViewPager viewPager, PagerAdapter adapter) {
        return new LoopPagerAdapter(viewPager, adapter);
    }

    public LoopPagerAdapter(ViewPager viewPager, PagerAdapter adapter) {
        this.mViewPager = viewPager;
        this.mPagerAdapter = adapter;

        viewPager.setAdapter(this);
        listenPageChanged();
        listenDataChanged();
        updateDefaultIndex();
    }

    private void listenPageChanged() {
        mViewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
                refollowIfNeeded(state);
            }
        });
    }

    private void listenDataChanged() {
        mPagerAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                notifyDataSetChanged();
            }
            public void onInvalidated() {
                notifyDataSetChanged();
            }
        });
    }

    private void updateDefaultIndex() {
        final int count = getCount();
        if (count < 2) return;

        final int defaultIndex = 1;
        boolean smoothScroll = false;
        mViewPager.setCurrentItem(
                defaultIndex,
                smoothScroll);
    }

    private void refollowIfNeeded(int state) {
        if (SCROLL_STATE_IDLE != state) return;

        int count = getCount();
        if (count < 2) return;

        int currentIndex = mViewPager
                .getCurrentItem();
        int index = -1;
        if (currentIndex == count - 1) {
            index = 1;
        } else if (currentIndex == 0) {
            index = count - 2;
        } else {
            return;
        }
        boolean smoothScroll = false;
        mViewPager.setCurrentItem(
                index,
                smoothScroll);
    }

    public final int swapIndex(int position) {
        int count = getCount();
        int realCount = getRealCount();
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
    public int getCount() {
        int count = getRealCount();
        if (count > 1) {
            count += 2;
        }
        return count;
    }

    public int getRealCount() {
        return mPagerAdapter.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return mPagerAdapter.isViewFromObject(view, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = (View) mPagerAdapter.instantiateItem(
                container, swapIndex(position));
        view.setTag(position);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mPagerAdapter.destroyItem(container, swapIndex(position), object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mPagerAdapter.setPrimaryItem(container, swapIndex(position), object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mPagerAdapter.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        mPagerAdapter.finishUpdate(container);
    }

    @Override
    public Parcelable saveState() {
        return mPagerAdapter.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        mPagerAdapter.restoreState(state, loader);
    }

    @Override
    public int getItemPosition(Object object) {
        return mPagerAdapter.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPagerAdapter.getPageTitle(position);
    }

    @Override
    public float getPageWidth(int position) {
        return mPagerAdapter.getPageWidth(swapIndex(position));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        updateDefaultIndex();
    }
}