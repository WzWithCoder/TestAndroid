package com.example.wangzheng.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Function;
import java.util.function.Supplier;

//https://www.toutiao.com/i7025859167827051038
@RequiresApi(api = Build.VERSION_CODES.N)
public final class Lazy<T> implements Supplier<T> {

    private final Supplier<? extends T> supplier;

    private T value;

    private Lazy(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    public final T get() {
        if (value == null) {
            value = supplier.get();
        }
        if (value == null) {
            throw new IllegalStateException("Lazy value can not be null!");
        }
        return value;
    }

    public <R> Lazy<R> map(Function<? super T, ? extends R> function) {
        return Lazy.of(() -> function.apply(get()));
    }

    public <R> Lazy<R> flatMap(Function<? super T, Lazy<? extends R>> function) {
        return Lazy.of(() -> function.apply(get()).get());
    }

    public <R> Lazy<R> apply(Lazy<Function<? super T, ? extends R>> function) {
        return Lazy.of(() -> function.get().apply(get()));
    }
}