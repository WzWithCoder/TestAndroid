package com.example.wangzheng.ui;

import static com.example.wangzheng.common.CommonKit.isPointInView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.spi.IServiceProvider;
import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.Debounced;
import com.example.wangzheng.common.SoftInputKit;
import com.example.wangzheng.common.TestKit;
import com.example.wangzheng.status_bar.StatusBar;
import com.example.wangzheng.widget.SimpleTextTip;
import com.example.wangzheng.widget.multi_input.Toolkit;
import com.example.wangzheng.widget.span.FixSpanClickable;
import com.example.wangzheng.widget.span.SpanBuilder;
import com.example.wangzheng.widget.span.TextSpan;
import com.example.wangzheng.widget.tv_fold.FoldTextView;
import com.example.wangzheng.widget.viewpager.AutoScrollViewPager;
import com.example.wangzheng.widget.viewpager.SnapIndicator;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.wz.arouter.BindViews;
import com.wz.arouter.annotation.InjectView;

import java.net.NetworkInterface;
import java.util.Iterator;
import java.util.ServiceLoader;

public class TestActivity extends AppCompatActivity {
    @InjectView(R.id.textView)
    public FoldTextView textView;
    @InjectView(R.id.viewPager)
    public AutoScrollViewPager viewPager;
    @InjectView(R.id.progressBar)
    public ProgressBar progressBar;

    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.setLightMode(this, 0xFFFFFFFF);
        setContentView(R.layout.activity_test_layout);
        BindViews.inject(this);

        try {
            final double factor = 2.18;
            CommonKit.setScrollFactor(viewPager, factor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new Adapter();
        viewPager.setAdapter(adapter, true);
        viewPager.setOffscreenPageLimit(3);
        viewPager.joinTimer(time-> {
            updateStatus(time);
            return true;
        }, 10);
        viewPager.start();

        SnapIndicator indicator = findViewById(R.id.simpleIndicator);
        indicator.setupViewpager(viewPager);
        textView.setText(TestKit.testSpan(textView));
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.actionHandle(o-> {
            CommonKit.toast("展开");
            return true;
        });
        textView.setSuffix(SpanBuilder.from("...")
                .append("展开文本", TextSpan.builder()
                                .setCorner(4)
                                .setBackgroundColor(Color.RED)
                        .setTextColor(Color.WHITE).setPadding(5, 2)
                        .setRatio(0.9f),
                new FixSpanClickable() {
            public void click(View v) {
                textView.foldAnimator(false);
            }
        }));

        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonKit.toast("onClick");
            }
        });

        TextView tipView = findViewById(R.id.tipView);
        tipView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SimpleTextTip.show(v, "测试测试测试测试测试");
            }
        });

        tipView.post(() -> {
            TransitionManager.beginDelayedTransition((ViewGroup) getWindow().getDecorView());
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tipView.getLayoutParams();
            lp.leftMargin = CommonKit.dip2px(20);
            tipView.setLayoutParams(lp);
        });
//        //BlurMaskFilter

        String url = "http://pic1.ymatou.com/G03/M02/F2/24/CgzUIF7h1aqAOw9bAAAlH3lJAuY076_121_46_o.png";
        Glide.with(this).load(url).into((ImageView) findViewById(R.id.aspectImageView));
        Debounced debounced = new Debounced();
        findViewById(R.id.aspectImageView).setOnClickListener(v -> {
            debounced.post(()-> {
                CommonKit.toast(v.getWidth() + "");
            });
        });

        Toolkit.listenKeyboard(this, isVisible-> {
            View view = getCurrentFocus();
            if (!isVisible && view != null) {
                view.clearFocus();
            }
        });

//        MappedByteBuffer mappedByteBuffer = new RandomAccessFile(file, "r")
//                .getChannel()
//                .map(FileChannel.MapMode.READ_ONLY, 0, len);

//        Bitmap bitmap = xml2bitmap(R.layout.xml2bitmap);
//        ImageView xxxx = findViewById(R.id.xxxxxx);
//        xxxx.setImageBitmap(bitmap);

        ServiceLoader<IServiceProvider> serviceLoader = ServiceLoader.load(IServiceProvider.class);
        Iterator<IServiceProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            IServiceProvider serviceProvider = iterator.next();
            serviceProvider.invoke("ImplServiceProvider");
        }

        TickerView tickerView = findViewById(R.id.tickerView);
        tickerView.setCharacterLists(TickerUtils.provideNumberList());
        tickerView.setText("$1234");
        tickerView.postDelayed(()-> {
            tickerView.setText(CommonKit.getMacFromHardware());
        }, 2000);

        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            Log.e("ll;;xsxs",element.toString());
        }
    }

    private Bitmap xml2bitmap(int resource) {
        View view = View.inflate(this, resource, null);
        view.setDrawingCacheEnabled(true);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(576, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final View v = getCurrentFocus();
        boolean isEditText = v instanceof EditText;
        if (isEditText && !isPointInView(v, ev)) {
            v.clearFocus();
            SoftInputKit.hideInputMethod(this, v);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateStatus(long p) {
        progressBar.setSecondaryProgress(1000 - (int) (1000f * p / 5000));
    }

    private class Adapter extends PagerAdapter {
         int[] resIds = {R.drawable.img9, R.drawable.timg,R.drawable.img3};


        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();

            ImageView view = new ImageView(context);
            int resid = resIds[position];
            container.addView(view);
            ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) view.getLayoutParams();
            layoutParams.width = layoutParams.MATCH_PARENT;
            if (resid == R.drawable.img9) {
                layoutParams.height = CommonKit.dip2px(150);
            } else if (resid == R.drawable.timg) {
                layoutParams.height = CommonKit.dip2px(200);
            } else if (resid == R.drawable.img3) {
                layoutParams.height = CommonKit.dip2px(180);
            }
            view.setLayoutParams(layoutParams);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setImageResource(resid);
            return view;
        }

//        @Override
//        public float getPageWidth(int position) {
//            return 0.5f;
//        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

}
