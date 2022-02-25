package com.example.wangzheng.common;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by wangzheng on 2016/1/28.
 */
public final class ActivityStack {
    private final static Stack<Activity> stack = new Stack<>();

    public static void pop() {
        Activity activity = stack.pop();
        if (activity != null &&
                !activity.isFinishing()) {
            activity.finish();
        }
    }

    public static void pop(final Activity activity) {
        if (activity != null) {
            stack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void push(final Activity activity) {
        stack.push(activity);
    }

    public static Activity peek() {
        return stack.peek();
    }

    public static void popAll() {
        while (!stack.empty()) {
            pop();
        }
    }

    public static int size() {
        return stack.size();
    }

    public static void exit() {
        ActivityStack.popAll();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
