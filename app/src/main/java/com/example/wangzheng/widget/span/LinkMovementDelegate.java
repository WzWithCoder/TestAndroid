package com.example.wangzheng.widget.span;

import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Create by wangzheng on 12/9/20
 */
public final class LinkMovementDelegate {

    public static boolean onTouchEvent(TextView widget
            , MotionEvent event) {
        //过滤触摸事件
        final int action = event.getAction();
        if (action != MotionEvent.ACTION_DOWN &&
            action != MotionEvent.ACTION_UP) {
            return false;
        }
        //获取触摸点的字符下标
        int index = touchToIndex(widget, event);
        if (index == -1) return false;
        //通过字符下标查找ClickableSpan
        ClickableSpan clickable = clickableByIndex(
                widget, index);
        if (clickable == null) return false;
        //手指抬起 触发点击事件
        if (action == MotionEvent.ACTION_UP) {
            clickable.onClick(widget);
        }
        return true;
    }

    public static ClickableSpan clickableByIndex(
            TextView widget, int index) {
        CharSequence charSequence = widget.getText();
        Spanned buffer = (Spanned) charSequence;
        final int end = index + 1;
        ClickableSpan[] links = buffer.getSpans(
                index, end,
                ClickableSpan.class);
        if (links != null && links.length > 0) {
            return links[0];
        }
        return null;
    }

    public static int touchToIndex(TextView widget
            , MotionEvent event) {
        if (widget == null) return -1;
        if (event == null) return -1;

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        final Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);

        if (pointInLine(x, y, layout, line)) {
            return findIndexByX(
                    widget,
                    layout,
                    line,
                    x);
        }
        return -1;
    }

    private static int findIndexByX(TextView widget
            , Layout layout, int line, int x) {
        final CharSequence text = widget.getText();
        int index = layout.getOffsetForHorizontal(line, x);
        //这里考虑省略号的问题，得到真实显示的字符串的长度，超过就返回 -1
        int ellipsCount = layout.getEllipsisCount(line);
        int count = text.length() - ellipsCount;
        if (index > count) return -1;
        //getOffsetForHorizontal 获得的下标会往右偏
        //获得下标处字符左边的坐标，如果大于点击的x，就可能点的是前一个字符
        float leftX = layout.getPrimaryHorizontal(index);
        if (leftX > x) {
            index -= 1;
        }
        return index;
    }

    private static boolean pointInLine(int x, int y
            , Layout layout, int line) {
        if (x < layout.getLineLeft(line))
            return false;
        if (x > layout.getLineRight(line))
            return false;
        if (y < layout.getLineTop(line))
            return false;
        if (y > layout.getLineBottom(line))
            return false;
        return true;
    }
}
