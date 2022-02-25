package com.example.wangzheng.binder;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

/**
 * Create on 2018/1/25 by wangzheng
 */

public interface AidlInterface<K, V> extends IInterface {

    public V method(K arg);

    public static abstract class Stub<K, V> extends Binder implements AidlInterface<K, V> {
        private static final String DESCRIPTOR = "AidlInterface";
        private static final int TRANSACTION_method = (IBinder.FIRST_CALL_TRANSACTION + 0);

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static AidlInterface asInterface(IBinder binder) {
            if ((binder == null)) {
                return null;
            }
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface != null && localInterface instanceof AidlInterface) {
                return (AidlInterface) localInterface;//同进程
            }
            return new AidlInterface.Stub.Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_method: {
                    data.enforceInterface(DESCRIPTOR);
                    try {
                        K arg = (K) data.readValue(getClass().getClassLoader());
                        V result = this.method(arg);
                        reply.writeNoException();
                        reply.writeValue(result);
                    } catch (Exception e) {
                        reply.writeException(e);
                    }
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public static class Proxy<K, V> implements AidlInterface<K, V> {
            private IBinder binder;

            public Proxy(IBinder binder) {
                this.binder = binder;
            }

            @Override
            public IBinder asBinder() {
                return binder;
            }

            @Override
            public V method(K arg){
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                V result = null;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeValue(arg);
                    binder.transact(Stub.TRANSACTION_method, data, reply, 0);
                    reply.readException();
                    result = (V) reply.readValue(getClass().getClassLoader());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
                return result;
            }
        }
    }
}
