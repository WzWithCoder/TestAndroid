package com.example.wangzheng.common;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangzheng on 2015/11/5.
 */
public abstract class BaseRecyclerAdapter<T>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    protected List<T> mList = new ArrayList<>();

    public List<T> list() {
        return mList;
    }

    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public T get(int position) {
        return mList.get(position);
    }

    public void add(List<T> list) {
        mList.addAll(list);
        int count = getItemCount();
        notifyItemRangeInserted(count,
                count + list.size() - 1);
    }

    public void add(T... list) {
        add(Arrays.asList(list));
    }

    public void add(T t) {
        mList.add(t);
        notifyItemInserted(mList.size() - 1);
    }

    public void add(int position, T t) {
        if (t != null && getItemCount() >= position) {
            mList.add(position, t);
            notifyItemInserted(position);
        }
    }

    public void remove(List<T> list) {
        if (mList.removeAll(list)) {
            notifyDataSetChanged();
        }
    }

    public void remove(T... list) {
        remove(Arrays.asList(list));
    }

    public void remove(T t) {
        remove(mList.indexOf(t));
    }

    public void remove(int index) {
        if (index >= 0 && index < mList.size()) {
            mList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final T data = mList.get(position);
        onBindViewHolder(viewHolder, position, data);

        if (mOnItemClickListener != null &&
                !viewHolder.itemView.hasOnClickListeners()) {
            viewHolder.itemView.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        final T data = mList.get(position);
        mOnItemClickListener.onItemClick(position, data);
    }

    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, T data);

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

}
