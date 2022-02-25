package com.example.wangzheng.uiHandleBridger;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Create on 2018/1/25 by wangzheng
 */

public interface ICallable<K, V> extends IInterface {

    V call(K arg);

    abstract class Stub<K, V> extends Binder implements ICallable<K, V> {
        protected static final String DESCRIPTOR = "Callable";
        private static final int TRANSACTION_CALL = IBinder.FIRST_CALL_TRANSACTION + 0;
        private ClassLoader classLoader;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
            classLoader = getClass().getClassLoader();
        }

        public static ICallable asInterface(IBinder binder) {
            if ((binder == null)) return null;
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface instanceof ICallable) {
                return (ICallable) localInterface;//同进程
            }
            return new ICallable.Stub.Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data
                , Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_CALL: {
                    onCallHandle(data, reply);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        protected void onCallHandle(Parcel data, Parcel reply) {
            try {
                data.enforceInterface(DESCRIPTOR);
                K arg = (K) data.readValue(classLoader);
                V result = this.call(arg);
                reply.writeNoException();
                reply.writeValue(result);
            } catch (Exception e) {
                reply.writeException(e);
            }
        }

        public static class Proxy<K, V> implements ICallable<K, V> {
            private IBinder binder;
            private ClassLoader classLoader;

            public Proxy(IBinder binder) {
                this.binder = binder;
                classLoader = getClass().getClassLoader();
            }

            @Override
            public IBinder asBinder() {
                return binder;
            }

            @Override
            public final V call(K args) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                V result = null;
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeValue(args);
                    binder.transact(Stub.TRANSACTION_CALL,
                            data, reply,
                            FLAG_ONEWAY);
                    reply.readException();
                    result = (V) reply.readValue(classLoader);
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
