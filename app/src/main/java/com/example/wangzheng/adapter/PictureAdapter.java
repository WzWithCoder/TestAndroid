package com.example.wangzheng.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.example.wangzheng.R;
import com.example.wangzheng.album.Builder;
import com.example.wangzheng.model.PictureEntity;
import com.example.wangzheng.widget.zoom_imageview.AnimatorUpdateListener;
import com.example.wangzheng.widget.zoom_imageview.BitmapsDrawable;
import com.example.wangzheng.widget.zoom_imageview.ZoomImageView;

import java.io.IOException;
import java.io.InputStream;

public class PictureAdapter extends PagerAdapter {
    private View mPrimaryView;
    private Builder mBuilder;
    protected AnimatorUpdateListener mAnimatorUpdateListener;

    public PictureAdapter(Builder builder,AnimatorUpdateListener listener) {
        this.mBuilder = builder;
        this.mAnimatorUpdateListener = listener;
    }

    private boolean flag = true;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        ZoomImageView imageView = (ZoomImageView) View.inflate(
                context,R.layout.item_zoom_image_view,null);
        PictureEntity data = mBuilder.pictures.get(position);

        imageView.setAnimatorUpdateListener(mAnimatorUpdateListener);
        imageView.setThumbRect(mBuilder.inRect(position));
        imageView.setEnableFade(position == mBuilder.index && flag);
        if (position == mBuilder.index) flag = false;
        /*if(data.drawableId == R.drawable.img6 || data.drawableId == R.drawable.img7){
            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open(
                        data.drawableId == R.drawable.img6 ? "hlarg.jpg" : "larg.jpg");
                imageView.setImageDrawable(new BitmapsDrawable(is));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }else */Glide.with(context)
                .load(data.drawableId)
                .priority(Priority.HIGH)
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return mBuilder.pictures.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryView = ((View) object).findViewById(R.id.imageView);
    }

    public View getPrimaryItem() {
        return mPrimaryView;
    }
}