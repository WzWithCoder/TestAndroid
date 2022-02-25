package com.example.wangzheng.binder;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

public class BinderCursor extends MatrixCursor {

    public static final String BINDER_KEY = "binder";

    Bundle mBinderExtra = new Bundle();


    public BinderCursor(String[] columnNames, IBinder binder) {
        super(columnNames);

        if (binder != null) {
            Parcelable value = new BinderParcel(binder);
            mBinderExtra.putParcelable(BINDER_KEY, value);
        }
    }

    @Override
    public Bundle getExtras() {
        return mBinderExtra;
    }

    public static final Cursor queryBinder(IBinder binder) {
        return new BinderCursor(new String[]{"id","binder"}, binder);
    }

    public static final IBinder getBinder(Cursor cursor) {
        Bundle extras = cursor.getExtras();
        extras.setClassLoader(BinderCursor.class.getClassLoader());
        BinderParcel w =  extras.getParcelable(BINDER_KEY);
        return w.getBinder();
    }
}