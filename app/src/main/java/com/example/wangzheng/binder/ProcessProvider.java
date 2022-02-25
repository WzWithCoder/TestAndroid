package com.example.wangzheng.binder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.example.wangzheng.binder.BinderCursor.BINDER_KEY;

/**
 * binder
 */
public class ProcessProvider extends SimpleContentProvider {

    private static final String TAG = "ProcessProvider";

    public static final String AUTHORITY_PREFIX = TAG + ".binder";

    public static final Uri buildUri(String key) {
        Uri uri = Uri.parse("content://" + AUTHORITY_PREFIX + key);
        return uri;
    }

    private static final Map<String, IBinder> mHashMap = new HashMap<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        IBinder binder = mHashMap.get(selection);
        return BinderCursor.queryBinder(binder);
    }

    @Override
    public Bundle call(@NonNull String method,
                       @Nullable String id,
                       @Nullable Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        if ("query".equals(method)) {
            IBinder binder = mHashMap.get(id);
            extras.putBinder(BINDER_KEY, binder);
        } else if ("update".equals(method) ||
                "insert".equals(method)) {
            IBinder binder = extras.getBinder(BINDER_KEY);
            mHashMap.put(id, binder);
        } else if ("delete".equals(method)) {
            mHashMap.remove(id);
        }
        return extras;
    }

    public void testInsert(Context context,IBinder binder){
        Uri uri = ProcessProvider.buildUri("");
        ContentResolver contentResolver = context.getContentResolver();
        Bundle bundle = new Bundle();
        bundle.putBinder(BINDER_KEY, binder);
        contentResolver.call(uri, "insert", "1", bundle);
    }
}