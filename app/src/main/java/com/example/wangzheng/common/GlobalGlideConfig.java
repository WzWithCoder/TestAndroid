package com.example.wangzheng.common;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by wangzheng on 2018/3/20.
 * <p>
 * Glide全局配置，使用GlideModule注解执行自动代码生成，生成GlideApp，后续的Glide
 * 调用都需要替换为GlideApp.with(context).load(url).into(imageView) 的方式
 * https://blog.csdn.net/shangmingchao/article/details/51125554/
 */
public class GlobalGlideConfig implements GlideModule {
    final int diskSize = 1024 * 1024 * 100;
    final int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;


    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setMemoryCache(new LruResourceCache(memorySize));
        builder.setBitmapPool(new LruBitmapPool(memorySize));
        File cacheFile = Storage.getCacheDir(Environment.DIRECTORY_PICTURES);
        builder.setDiskCache(new DiskLruCacheFactory(
                cacheFile.getAbsolutePath(), diskSize));
    }


    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
