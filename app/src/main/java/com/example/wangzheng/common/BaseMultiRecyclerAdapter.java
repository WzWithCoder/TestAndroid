package com.example.wangzheng.common;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * Create by wangzheng on 2018/7/4
 */
public abstract class BaseMultiRecyclerAdapter<T> extends BaseRecyclerAdapter<WrapMultiItem<T>> {

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = ((GridLayoutManager) layoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int position) {
                    WrapMultiItem item = get(position);
                    return item.spanSise == -1 ? gridLayoutManager.getSpanCount() : item.spanSise;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams
                && get(holder.getLayoutPosition()).spanSise >= 1) {
            StaggeredGridLayoutManager.LayoutParams lp =
                    (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            lp.setFullSpan(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        WrapMultiItem wrapData = get(position);
        return wrapData.type;
    }

    public void addWrapData(int type, T... list) {
        for (T t : list) {
            addWrapData(t, type);
        }
    }

    public void addWrapData(int type, int spanSise, T... list) {
        for (T t : list) {
            addWrapData(t, type, spanSise);
        }
    }

    public void addWrapData(T t, int type) {
        add(new WrapMultiItem(t, type, -1));
    }

    public void addWrapData(T t, int type, int spanSise) {
        add(new WrapMultiItem(t, type, spanSise));
    }
}
