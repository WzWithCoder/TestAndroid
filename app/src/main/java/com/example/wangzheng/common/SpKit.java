package com.example.wangzheng.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.wangzheng.App;

import java.util.Map;
import java.util.Set;

/**
 * Create by wangzheng on 2018/7/25
 */
public final class SpKit {
    private static final String TAG = SpKit.class.getSimpleName();
    private SharedPreferences sp;

    private SpKit(final String name) {
        this(name, Context.MODE_PRIVATE);
    }

    private SpKit(final String name, final int mode) {
        sp = App.instance().getSharedPreferences(name, mode);
    }

    public static SpKit from(final String name) {
        return new SpKit(name);
    }

    public SpKit put(final String key,
                     final Object value) {
        Inner.put(sp, key, value);
        return this;
    }

    public <T> T get(final String key,
                     final T defValue) {
        return Inner.get(sp, key, defValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public SpKit remove(final String key) {
        sp.edit().remove(key).commit();
        return this;
    }

    public SpKit clear() {
        sp.edit().clear().commit();
        return this;
    }

    public boolean contains(final String key) {
        return sp.contains(key);
    }

    public final static SpKit Default = SpKit.from(
            App.instance().getPackageName());

    public static SpKit write(final String key,
                              final Object value) {
        Default.put(key, value);
        return Default;
    }

    public static <T> T read(final String key,
                             final T defValue) {
        return Default.get(key, defValue);
    }

    public static final class Inner {
        //Properties.java
        //SharedPreferences

        public final static void put(final SharedPreferences sp,
                                     final String key,
                                     final Object value) {
            SharedPreferences.Editor editor = sp.edit();
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Set) {
                editor.putStringSet(key, (Set<String>) value);
            } else {
                Log.w(TAG, value + " put failed");
            }
            editor.commit();
        }

        public final static <T> T get(final SharedPreferences sp,
                                      final String key,
                                      final T defValue) {
            Object value = null;
            if (defValue instanceof String) {
                value = sp.getString(key, (String) defValue);
            } else if (defValue instanceof Integer) {
                value = sp.getInt(key, (Integer) defValue);
            } else if (defValue instanceof Boolean) {
                value = sp.getBoolean(key, (Boolean) defValue);
            } else if (defValue instanceof Float) {
                value = sp.getFloat(key, (Float) defValue);
            } else if (defValue instanceof Long) {
                value = sp.getLong(key, (Long) defValue);
            } else if (defValue instanceof Set) {
                value = sp.getStringSet(key, (Set<String>) defValue);
            } else {
                Log.w(TAG, value + " get failed");
            }
            return (T) value;
        }
    }
}
