package com.example.wangzheng.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class LayoutAnimator {
    public static Animator ofItemViewHeight(boolean isExpanded
            , RecyclerView.ViewHolder viewHolder) {
        View itemView = viewHolder.itemView;
        itemView.setVisibility(View.VISIBLE);

        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        int paramWidth = layoutParams.width;
        int paramHeight = layoutParams.height;

        int height = itemView.getHeight();
        int start = isExpanded ? height : 0;
        int end = isExpanded ? 0 : height;
        Animator animator = LayoutAnimator.ofHeight(itemView, start, end);
        //设置itemView能被复用
        animator.addListener(new ViewHolderAnimatorListener(viewHolder));
        //结束时重置view的属性
        int visibility = isExpanded ? View.GONE : View.VISIBLE;
        animator.addListener(new LayoutParamsAnimatorListener(
                itemView, visibility, paramWidth, paramHeight));
        return animator;
    }

    public static Animator ofHeight(View view, int start, int end) {
        final ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new LayoutHeightUpdateListener(view));
        return animator;
    }

    public static class LayoutHeightUpdateListener
            implements ValueAnimator.AnimatorUpdateListener {
        private final View view;

        public LayoutHeightUpdateListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator a) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) a.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        }
    }

    public static class ViewHolderAnimatorListener extends AnimatorListenerAdapter {
        private RecyclerView.ViewHolder viewHolder;

        public ViewHolderAnimatorListener(RecyclerView.ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            viewHolder.setIsRecyclable(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            viewHolder.setIsRecyclable(true);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            viewHolder.setIsRecyclable(true);
        }
    }


    public static class LayoutParamsAnimatorListener extends AnimatorListenerAdapter {
        private final View view;
        private final int visibility;
        private final int paramWidth;
        private final int paramHeight;

        public LayoutParamsAnimatorListener(View view
                , int visibility, int paramWidth, int paramHeight) {
            this.view = view;
            this.visibility = visibility;
            this.paramWidth = paramWidth;
            this.paramHeight = paramHeight;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = paramWidth;
            params.height = paramHeight;
            view.setLayoutParams(params);
            view.setVisibility(visibility);
        }
    }
}