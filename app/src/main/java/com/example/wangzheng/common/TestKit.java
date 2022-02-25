package com.example.wangzheng.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import androidx.collection.LruCache;

import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Printer;
import android.view.Choreographer;
import android.view.View;
import android.widget.TextView;

import com.example.wangzheng.App;
import com.example.wangzheng.R;
import com.example.wangzheng.Textkit;
import com.example.wangzheng.binder.AidlInterface;
import com.example.wangzheng.binder.RemoteService;
import com.example.wangzheng.http.HttpRequest;
import com.example.wangzheng.http.callback.StringCallback;
import com.example.wangzheng.widget.multi_input.MultiInputWindow;
import com.example.wangzheng.widget.multi_input.MultiInputWindow1;
import com.example.wangzheng.widget.multi_input.MultiInputWindow2;
import com.example.wangzheng.widget.multi_input.MultiInputWindow3;
import com.example.wangzheng.widget.span.composer.build.CompBuilder;
import com.example.wangzheng.widget.span.composer.build.ImageBuilder;
import com.example.wangzheng.widget.span.composer.SpanComposer;
import com.example.wangzheng.widget.span.composer.build.Builder;
import com.example.wangzheng.widget.span.composer.TextSpan;
import com.example.wangzheng.widget.tv_fold.FoldTextView;
import com.example.wangzheng.widget.span.FixSpanClickable;
import com.example.wangzheng.widget.span.ImageSpan;
import com.example.wangzheng.widget.span.SpanBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Create by wangzheng on 2018/6/20
 */
//CrazyShadow
public class TestKit {
    public static long calculateCache(LruCache<String, WeakReference<Bitmap>> cache) {
        long size = 0;
        Bitmap bitmap;
        Map<String, WeakReference<Bitmap>> map = cache.snapshot();
        for (WeakReference<Bitmap> reference : map.values()) {
            bitmap = reference.get();
            if (bitmap != null) {
                size += bitmap.getByteCount();
            }
        }
        return size;
    }

    public static void testBinder(Context context, final TextView textView) {
        Intent service = new Intent(context, RemoteService.class);
        context.bindService(service, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AidlInterface<Integer, String> aidlInterface = AidlInterface.Stub.asInterface(service);
                String remoteResult = aidlInterface.method(1);
                textView.append("\n");
                textView.append(remoteResult);
                textView.append("\n");
                textView.append("client pid : " + android.os.Process.myPid());
                textView.append("\n");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    public static SpanBuilder testSpan(FoldTextView tv) {
        String gif = "http://app.ymatou.com/api/nodesocial/socialcircle?pageIndex=9";
        Builder<SpanComposer> bubbleBuilder = new CompBuilder()
                .style(Paint.Style.FILL)
                .align(0f)
                .bgColor(Color.RED)
                .hMargin(1)
                .radius(20, 3, 20, 20)
                .padding(10, 2);
        bubbleBuilder.join(TextSpan.newBuilder()
                .text("券后¥")
                .color(Color.BLUE)
                .fakeBold(true)
                .align(1f)
                .size(9)
                .rightMargin(3))
                .join(TextSpan.newBuilder()
                        .text("682")
                        .fakeBold(true)
                        .color(Color.BLACK)
                        .strikeLine()
                        .size(24)
                        .align(0.5f))
                .join(TextSpan.newBuilder()
                        .text(".78起")
                        .color(Color.BLACK)
                        .fakeBold(true)
                        .size(9)
                        .leftMargin(2)
                        .align(0f));
        SpanComposer spanComposer = bubbleBuilder.build();
        SpanBuilder spanBuilder = new SpanBuilder();
        Textkit.applyPrice(spanBuilder, "149.12起", Color.RED);

        String utl = "http://pic1.ymatou.com/G03/M02/F2/24/CgzUIF7h1aqAOw9bAAAlH3lJAuY076_121_46_o.png";
        Builder<SpanComposer> bubbleBuilder2 = new CompBuilder().hPadding(3);
        Builder imageBuilder = new ImageBuilder()
                .anchor(tv)
                .url(utl)
                .size(14)
                .radius(3);
        bubbleBuilder2.join(imageBuilder);

        return spanBuilder.append("包邮包税",
                com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.STROKE)
                        .setCorner(4).setBackgroundColor(Color.RED)
                        .setTextColor(Color.RED).setPadding(2, 2)
                        .setStrokeWidth(2.0f).setRatio(0.95f),
                new ClickableSpan() {
                    public void onClick(View widget) {
                        Activity activity = CommonKit.canForActivity(
                                widget.getContext());
                        MultiInputWindow3.show(activity);
                    }
                })
                .append("#优选#", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.RED).setRatio(0.9f)
                        .setTextColor(Color.WHITE).setPadding(7, 4).setMargin(6, 6))
                .append("预售", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.parseColor("#419BF9")).setRatio(0.75f)
                        .setTextColor(Color.WHITE).setPadding(7, 4).setMargin(0, 0))
                .append("新品", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.parseColor("#F57A6D")).setRatio(0.75f)
                        .setTextColor(Color.WHITE).setPadding(7, 4).setMargin(6, 0))
                .append("直播", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.parseColor("#F9B164")).setRatio(0.75f)
                        .setTextColor(Color.WHITE).setPadding(7, 4)
                        .setCorner(0).setMargin(6, 6))
                .append("现货", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.parseColor("#419BF9")).setRatio(1.0f)
                        .setTextColor(Color.WHITE).setPadding(6, 2).setCorner(5).setMargin(0, 0))
                .append("图片", new ImageSpan(App.instance(), R.drawable.img3).ratio(1.1f).margin(6, 0))
                .append("图片", new ImageSpan(tv, "http://pic1.ymatou.com/G03/M02/F2/24/CgzUIF7h1aqAOw9bAAAlH3lJAuY076_121_46_o.png")
                        .ratio(1.2f).margin(6, 0))
                .append("图片", new ImageSpan(tv, gif).ratio(1.2f).margin(6, 0))
                .append("图片", new ImageSpan(App.instance(), R.drawable.img9).ratio(1.1f).radius(3).margin(6, 0))
                .append("商品描述")
                .append("1234", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL)
                        .setCorner(7).setBackgroundColor(Color.parseColor("#F57A6D")).setRatio(0.85f)
                        .setTextColor(Color.WHITE).setPadding(7, 4).setMargin(6, 6))
                .append("商品描述商品描述商品描述商品描述商品描述商品描述商品描述商品")
                .append("商品价格", spanComposer)
                .append("商品描述商品描述商品描述商品描述商品描述商品描述商品描述")
                .append("商品价格", bubbleBuilder2.build())
                .append("商品描述商品描述商品描述商品描述商品描述商品描述商品描述")
                .append("收起", com.example.wangzheng.widget.span.TextSpan.builder().setStyle(Paint.Style.FILL_AND_STROKE)
                                .setCorner(4).setBackgroundColor(Color.RED)
                                .setTextColor(Color.WHITE).setPadding(5, 2)
                                .setMargin(15, 0).setRatio(0.9f),
                        new FixSpanClickable() {
                            public void click(View widget) {
                                tv.foldAnimator(true);
                            }
                        });
    }

    public static void testRx() {
        Observable.create((Subscriber<? super File> subscriber)-> {
            subscriber.onStart();
            subscriber.onNext(Environment.getExternalStorageDirectory());
            subscriber.onCompleted();
        })
        .filter(file -> file != null)
        .map(file-> file.listFiles())
        .filter(files -> files != null)
        .flatMap(files-> Observable.from(files))
        .map(file -> file.getAbsolutePath())
        .filter(path-> path.endsWith(".png"))
        .map(path -> BitmapFactory.decodeFile(path))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidScheduler.mainThread())
        .subscribe(bitmap -> {

        }, e-> {

        });
    }

    public static void scanLocalImages(BaseRecyclerAdapter adapter) {
        new AsyncTask<File, String, List<File>>() {
            @Override
            protected List<File> doInBackground(File... params) {
                List<File> list = new LinkedList<>();
                Stack<File> stack = new Stack<>();
                stack.push(params[0]);
                while (!stack.empty()) {
                    File temp = stack.pop();
                    if (temp == null) {
                        continue;
                    }
                    String name = temp.getName();
                    if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))
                        publishProgress(temp.getAbsolutePath());
                    list.add(temp);
                    File[] files = temp.listFiles();
                    if (files == null) {
                        continue;
                    }
                    for (File f : files) {
                        stack.push(f);
                    }
                }
                return list;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                adapter.add(values[0]);

            }
        }.execute(Environment.getExternalStorageDirectory());
    }

    private static List<File> queryLocalFiles(File parent) {
        List<File> list = new LinkedList<>();
        Stack<File> stack = new Stack<>();
        stack.push(parent);
        while (!stack.empty()) {
            File temp = stack.pop();
            if (temp == null) {
                continue;
            }
            list.add(temp);
            File[] files = temp.listFiles();
            if (files == null) {
                continue;
            }
            for (File f : files) {
                stack.push(f);
            }
        }
        return list;
    }

    public static void localhost() {
        String url = "http://127.0.0.1:8088/ad/Activity";
        Map<String, String> params = new HashMap<>();
        params.put("1", "ssss");
        params.put("232", "ssss");
        params.put("6565", "ssss");
        HttpRequest.get(url, params, new StringCallback() {
            public void onSuccess(String data) {
                CommonKit.toast(data);
            }

            public void onError(String msg) {
                CommonKit.toast(msg);
            }
        });
    }

    public static void testFrame() {
        Choreographer.getInstance().postFrameCallback(
                new Choreographer.FrameCallback() {
                    public void doFrame(long frameTimeNanos) {
                        Log.e("Looper doFrame", frameTimeNanos + "");
                    }
                });
        Looper.getMainLooper().setMessageLogging(new Printer() {
            long startTime = 0;
            long endTime = 0;
            int index = 0;

            public void println(String x) {
                Log.d("Looper", x);
                long time = System.currentTimeMillis();
                if (x.contains("Dispatching")) {
                    startTime = time;
                } else if (x.contains("Finished")) {
                    endTime = time;
                }
                if (startTime > 0 && endTime > 0) {
                    Log.e("Looper Frame",
                            ++index + "-" + (endTime - startTime));
                    startTime = 0;
                    endTime = 0;
                }
            }
        });
    }
}
