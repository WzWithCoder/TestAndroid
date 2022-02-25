package com.example.wangzheng.nested_scroll;

import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Create by wangzheng on 2018/5/16
 */
public class NestedScrollKit {
    public static boolean canChildScroll(View view) {
        if(view instanceof NestedScrollChild){
            return ((NestedScrollChild) view).canScroll();
        }
        return false;
    }

    public static View findChildViewUnder(ViewGroup viewGroup, float x, float y) {
        x += viewGroup.getScrollX();
        y += viewGroup.getScrollY();
        final int count = viewGroup.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();
            if (x >= child.getLeft() + translationX &&
                    x <= child.getRight() + translationX &&
                    y >= child.getTop() + translationY &&
                    y <= child.getBottom() + translationY) {
                return child;
            }
        }
        return null;
    }

    public static ViewParent findNestedScrollParent(View view) {
        ViewParent parent = view.getParent();
        if(parent instanceof NestedScrollParent){
            return parent;
        }else{
            return findNestedScrollParent((View) parent);
        }
    }

    public static int getOrientation(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? 'l' : 'r';
        } else {
            return dy > 0 ? 'u' : 'd';
        }
    }
}
