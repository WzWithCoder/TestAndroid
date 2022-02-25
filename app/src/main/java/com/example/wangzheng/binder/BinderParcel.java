package com.example.wangzheng.binder;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public class BinderParcel implements Parcelable {

    private IBinder mBinder;

    public static final Parcelable.Creator<BinderParcel>
            CREATOR = new Parcelable.Creator<BinderParcel>() {
        @Override
        public BinderParcel createFromParcel(Parcel source) {
            return new BinderParcel(source);
        }
        @Override
        public BinderParcel[] newArray(int size) {
            return new BinderParcel[size];
        }
    };

    public BinderParcel(IBinder binder) {
        mBinder = binder;
    }

    public BinderParcel(Parcel source) {
        mBinder = source.readStrongBinder();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(mBinder);
    }

    public IBinder getBinder() {
        return mBinder;
    }
}