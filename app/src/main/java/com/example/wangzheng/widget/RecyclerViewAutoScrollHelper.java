package com.example.wangzheng.widget;

import androidx.core.widget.AutoScrollHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAutoScrollHelper extends AutoScrollHelper {

    protected RecyclerView mTarget;

    public RecyclerViewAutoScrollHelper(RecyclerView target) {
        super(target);
        this.mTarget = target;
    }

    public static AutoScrollHelper install(RecyclerView target) {
        AutoScrollHelper autoScrollHelper = new RecyclerViewAutoScrollHelper(target);
        autoScrollHelper.setEnabled(true);
        target.setOnTouchListener(autoScrollHelper);
        return autoScrollHelper;
    }

    @Override
    public void scrollTargetBy(int deltaX, int deltaY) {
        mTarget.scrollBy(deltaX, deltaY);
    }


    @Override
    public boolean canTargetScrollHorizontally(int direction) {
        return mTarget.getLayoutManager().canScrollHorizontally();
    }


    @Override
    public boolean canTargetScrollVertically(int direction) {
        return mTarget.getLayoutManager().canScrollVertically();
    }

}