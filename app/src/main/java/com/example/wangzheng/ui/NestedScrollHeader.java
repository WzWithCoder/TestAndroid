package com.example.wangzheng.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.wangzheng.R;
import com.example.wangzheng.common.BaseRecyclerAdapter;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.TestKit;
import com.example.wangzheng.common.ViewOffsetHelper;
import com.example.wangzheng.status_bar.StatusBar;
import com.example.wangzheng.widget.AspectImageView;
import com.example.wangzheng.widget.TopShadow;
import com.example.wangzheng.widget.round.BubbleTextView;
import com.example.wangzheng.widget.round.RoundImageView;
import com.google.android.material.appbar.AppBarLayout;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;


public class NestedScrollHeader extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.setLightMode(this, 0x0);
        setContentView(R.layout.activity_nested_scroll_header);

        RecyclerView rv = findViewById(R.id.list);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view
                    , RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = outRect.right = outRect.bottom = CommonKit.dip2px(2);
            }
        });
        rv.addItemDecoration(new TopShadow());
        rv.setLayoutManager(new StaggeredGridLayoutManager(8,VERTICAL));
        BaseRecyclerAdapter adapter;
        Drawable placeholder = new ColorDrawable(Color.YELLOW);
        rv.setAdapter(adapter = new BaseRecyclerAdapter<String>() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Holder(new AspectImageView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, String data) {
                Holder h = (Holder) viewHolder;
                Glide.with(h.imageView.getContext()).load(data).placeholder(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).into(h.imageView);
            }

            class Holder extends RecyclerView.ViewHolder {
                AspectImageView imageView;

                public Holder(AspectImageView iv) {
                    super(iv);
                    imageView = iv;
                    imageView.setRadius(8);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setBackgroundColor(Color.WHITE);
                    imageView.setLayoutParams(new RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                }
            }
        });
        TestKit.scanLocalImages(adapter);
    }
}
