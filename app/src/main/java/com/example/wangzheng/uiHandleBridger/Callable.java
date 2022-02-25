package com.example.wangzheng.uiHandleBridger;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.wangzheng.binder.BinderParcel;

public abstract class Callable<K, V> extends ICallable.Stub<K, V> implements ICallable<K, V> {
    private final static String BINDER = "CALL_BINDER_KEY";
    private final BinderParcel mBinderParcel;

    public Callable() {
        mBinderParcel = new BinderParcel(this);
    }

    public Callable linkTo(Intent intent) {
        intent.putExtra(BINDER, mBinderParcel);
        return this;
    }

    public static <K, V> ICallable<K, V> with(Intent intent) {
        BinderParcel parcel = intent.getParcelableExtra(BINDER);
        IBinder binder = parcel.getBinder();
        return ICallable.Stub.asInterface(binder);
    }

    public static <K, V> Callable of(Intent intent, Call<K, V> call) {
        Callable callable = new Callable<K, V>() {
            public V call(K args) {
                return call.invoke(args);
            }
        }.linkTo(intent);
        return callable;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}