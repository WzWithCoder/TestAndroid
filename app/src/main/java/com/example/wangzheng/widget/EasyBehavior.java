package com.example.wangzheng.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.ViewOffsetHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class EasyBehavior extends CoordinatorLayout.Behavior<TextView> {//这里的泛型是child的类型，也就是观察者View

    public EasyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int scrollRange = 0;
    private int topMargin = 0;
    private int heightBanner;
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        //告知监听的dependency是banner
        if (dependency instanceof AppBarLayout) {
            View left = dependency.findViewById(R.id.left);
            if (topMargin <= 0) {
                topMargin = left.getTop() + (left.getHeight() - child.getHeight()) / 2;
                scrollRange = left.getBottom() + CommonKit.dip2px(child.getContext(), 10);
                heightBanner = dependency.findViewById(R.id.banner).getHeight();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    //当 dependency(Button)变化的时候，可以对child(TextView)进行操作
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {

        AppBarLayout abl = (AppBarLayout) dependency;
        float ratio = 1f * abl.getTop() / abl.getTotalScrollRange();
        ratio = Math.abs(ratio);
        int margin = (int) (topMargin * ratio);

        View leftView = dependency.findViewById(R.id.left);
        int left = leftView.getRight();
        left = (int) (left * ratio) + CommonKit.dip2px(child.getContext(), 10);

        View rightView = dependency.findViewById(R.id.right);
        int right = dependency.getWidth() - rightView.getLeft();
        right = (int) (right * ratio)  + CommonKit.dip2px(child.getContext(), 10);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.leftMargin = left;
        lp.rightMargin = right;
        child.setLayoutParams(lp);

        ratio = 1f - ratio;

        int scrollOffset = (int) (scrollRange * ratio) + margin;
        child.setY(scrollOffset);
        //child.setText(child.getX() + "," + child.getY() + "," + ratio);
        return true;
    }


    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull TextView child, @NonNull View directTargetChild, @NonNull View target,
                                       @ViewCompat.ScrollAxis int axes, @ViewCompat.NestedScrollType int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    private int maxHeight = CommonKit.dip2px(500);

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout
            , @NonNull TextView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        AppBarLayout appBarLayout = coordinatorLayout.findViewById(R.id.appBarLayout);
        ImageView banner = coordinatorLayout.findViewById(R.id.banner);
        int height = banner.getHeight();
        if (appBarLayout.getTop() == 0 && dy < 0 && height < maxHeight && type == ViewCompat.TYPE_TOUCH) {
            CollapsingToolbarLayout.LayoutParams lp =
                    (CollapsingToolbarLayout.LayoutParams)
                            banner.getLayoutParams();
            lp.height -= dy;
            banner.setLayoutParams(lp);
            consumed[1] += dy;
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout
            , @NonNull TextView child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        ImageView banner = coordinatorLayout.findViewById(R.id.banner);
        int height = banner.getHeight();
        if (height > heightBanner) {
            ValueAnimator animator = ValueAnimator.ofInt(height, heightBanner);
            animator.addUpdateListener(animation-> {
                int value = (int) animation.getAnimatedValue();
                CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams)
                        banner.getLayoutParams();
                lp.height = value;
                banner.setLayoutParams(lp);
            });
            animator.start();
        }
    }
}