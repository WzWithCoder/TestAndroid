package com.example.wangzheng.album;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.wangzheng.R;
import com.example.wangzheng.adapter.PictureAdapter;
import com.example.wangzheng.widget.CirclePageIndicator;
import com.example.wangzheng.widget.zoom_imageview.AnimatorUpdateListener;
import com.wz.arouter.BindViews;
import com.wz.arouter.annotation.InjectView;
import com.wz.arouter.annotation.Route;

@Route("BrowsePictureActivity")
public class BrowsePictureActivity extends Activity implements View.OnClickListener {
    @InjectView(com.example.wangzheng.R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.indicator)
    CirclePageIndicator indicator;
    @InjectView(Window.ID_ANDROID_CONTENT)
    View backgroundView;

    private Builder mBuilder;
    private PictureAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //去除半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); //全屏显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
//                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browes_picture_layout);
        BindViews.inject(this);

        mBuilder = Builder.from(this);
        mAdapter = new PictureAdapter(mBuilder, new AnimatorUpdateListener() {
            public void onAnimationUpdate(float fraction) {
                indicator.setAlpha(fraction);
            }
        });
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(mBuilder.index);
        indicator.setViewPager(viewPager);
        indicator.setVisibility(mAdapter.getCount() > 1
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
