package com.example.wangzheng.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.wangzheng.common.AppLifecycleMonitor.AppSwitchListener.BACKGROUND;
import static com.example.wangzheng.common.AppLifecycleMonitor.AppSwitchListener.FOREGROUND;

/**
 * Created by wangzheng on 2016/9/28.
 */

public class AppLifecycleMonitor extends ActivityLifecycle {
    private static AppLifecycleMonitor instance       = null;
    private static int                 mActivityCount = 0;

    private List<AppSwitchListener> mAppSwitchListeners = null;
    private Map<Activity, List<Application.ActivityLifecycleCallbacks>>
            mActivityLifecycleListeners = new HashMap<>();


    private AppLifecycleMonitor() {
        addSwitchListener(type-> {

        });
    }

    public synchronized static AppLifecycleMonitor instance() {
        if (instance == null) {
            instance = new AppLifecycleMonitor();
        }
        return instance;
    }

    public static void apply(Application context) {
        if (instance != null) {
            context.unregisterActivityLifecycleCallbacks(instance);
        }
        context.registerActivityLifecycleCallbacks(instance());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityStack.push(activity);
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityCreated(activity, savedInstanceState);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivityCount++ == 0) {
            frontAndBackSwitch(FOREGROUND);
        }
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityStarted(activity);
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityResumed(activity);
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityPaused(activity);
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (--mActivityCount <= 0) {
            frontAndBackSwitch(BACKGROUND);
        }
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityStopped(activity);
            }
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStack.pop(activity);
        if (activity instanceof AppSwitchListener) {
            removeChangeListener(
                    (AppSwitchListener) activity);
        }
        if (mActivityLifecycleListeners.containsKey(activity)) {
            List<Application.ActivityLifecycleCallbacks> lifecycleCallbacks
                    = mActivityLifecycleListeners.get(activity);
            for (Application.ActivityLifecycleCallbacks callback : lifecycleCallbacks) {
                callback.onActivityDestroyed(activity);
            }
            mActivityLifecycleListeners.remove(activity);
        }
    }

    public void frontAndBackSwitch(int type) {
        if (mAppSwitchListeners == null ||
                mAppSwitchListeners.isEmpty()) return;
        for (AppSwitchListener listener : mAppSwitchListeners) {
            try {
                listener.onSwitch(type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isRunningFront() {
        return mActivityCount > 0;
    }

    public void addSwitchListener(AppSwitchListener appSwitchListener) {
        if (mAppSwitchListeners == null) {
            mAppSwitchListeners = new ArrayList<>();
        }
        if (!mAppSwitchListeners.contains(appSwitchListener)) {
            mAppSwitchListeners.add(appSwitchListener);
        }
    }

    public void removeChangeListener(AppSwitchListener appSwitchListener) {
        if (mAppSwitchListeners == null || appSwitchListener == null) return;
        if (mAppSwitchListeners.contains(appSwitchListener)) {
            mAppSwitchListeners.remove(appSwitchListener);
        }
    }

    public void addActivityLifecycle(Activity activity
            , Application.ActivityLifecycleCallbacks callbacks) {
        if (activity == null || callbacks == null) return;
        List<Application.ActivityLifecycleCallbacks> list =
                mActivityLifecycleListeners.get(activity);
        if (list == null) {
            list = new ArrayList<>();
            mActivityLifecycleListeners.put(activity, list);
        }
        list.add(callbacks);
    }

    public void removeActivityLifecycle(Activity activity) {
        if (activity == null) return;
        mActivityLifecycleListeners.remove(activity);
    }

    public interface AppSwitchListener {
        int FOREGROUND     = 1,//前台
                BACKGROUND = 0;//后台

        void onSwitch(int type);
    }
}
