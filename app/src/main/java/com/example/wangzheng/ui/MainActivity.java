package com.example.wangzheng.ui;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.LayoutInflaterCompat;

import com.example.wangzheng.ActivityHandler;
import com.example.wangzheng.HotFixMethod;
import com.example.wangzheng.R;
import com.example.wangzheng.common.ActivityStack;
import com.example.wangzheng.common.Api;
import com.example.wangzheng.common.AsyncRun;
import com.example.wangzheng.common.BasicAdapter;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.PermissionChecker;
import com.example.wangzheng.common.TestKit;
import com.example.wangzheng.uiHandleBridger.Callable;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.skin.SkinInflaterFactory;
import com.wz.api.V8Bridger;
import com.wz.arouter.BindViews;
import com.wz.arouter.annotation.InjectView;
import com.wz.arouter.annotation.Route;
import com.ymt.server.HttpServer;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import demo.example.com.accessibility_service.ToolKit;
import opengl.TestOpenGLActivity;

///Object的hashCode()默认是返回内存地址的，但是hashCode()可以重写，所以hashCode()不能代表内存地址的不同
///System.identityHashCode(Object)方法可以返回对象的内存地址,不管该对象的类是否重写了hashCode()方法。

///Debug.dumpHprofData();

//https://github.com/googlesamples
//https://github.com/Blankj/AndroidUtilCode/blob/master/README-CN.md
@Route("MainActivity")
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.email_sign_in_button)
    public Button mButtonView;
    @InjectView(R.id.result_text)
    public ListView mListView;
    @InjectView(R.id.textview)
    public TextView mTextview;
    @InjectView(R.id.textview2)
    public TextView mTextview2;

    HttpServer httpServer = null;

    BasicAdapter<Entity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater()
                , new SkinInflaterFactory());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        BindViews.inject(this);

        //initXhookFromJNI();

        Looper.myQueue().addIdleHandler(()-> {
            return false;
        });

        PermissionChecker.from(this)
                .checkPermission(o -> initView(),
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
        try {
            V8Bridger.registeNativeApi();
            final String exp = "46+23-90+8*5+(1+1)";
            double result = V8Bridger.java2Js_eval(exp);
            Log.e("V8Bridger","java2Js:eval:"+result);
            V8Bridger.js2Java_println("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //HotFixMethod.test();

        //ActivityCompat.setPermissionCompatDelegate();
    }


    private void initView() {
        mButtonView.setOnClickListener(v -> {
            startActivity(new Intent(v.getContext(), TestActivity.class));
        });
        HttpServer.Builder builder = HttpServer.createBuilder()
                .registeHandler(new ActivityHandler(this));
        httpServer = builder.build();
        httpServer.start();

        //InetAddress.getAllByName("www.kugou.com");
        //mTextview.setText(NativeLib.callHello("hello jni world"));
        //NativeLib.callOnLoad("callOnLoad");
        TestKit.testBinder(this, mTextview);
        //mTextview2.append(TestKit.testSpan(mTextview));

        mListView.setOnItemClickListener(this);
        mListView.setAdapter(adapter = new BasicAdapter<Entity>(this) {
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = new TextView(mContext);
                tv.setPadding(20, 20, 20, 20);
                tv.setText(getItem(position).name);
                return tv;
            }
        });
        adapter.addList(
                new Entity("BrowsePicture"),
                new Entity("ListenLocalPort"),
                new Entity("NestedScrollHeader",
                        NestedScrollHeader.class),
                new Entity("NestedScrollActivity",
                        NestedScrollActivity.class),
                new Entity("ChatActivity",
                        ChatActivity.class),
                new Entity("WebViewActivity"),
                new Entity("AccessibilityService"),
                new Entity("TestRecyclerViewActivity",
                        TestRecyclerViewActivity.class),
                new Entity("OpenGL",
                        TestOpenGLActivity.class)
        );
        //new ScanMediaTask().execute();

        Api.Test.test();
//        TrafficStats.getTotalRxBytes()
//        ConnectionClassManager.getInstance().register(
//                cq-> CommonKit.toast(cq.name()));
//        DeviceBandwidthSampler.getInstance().startSampling();

        String path = CommonKit.getRecentImage(this,
                System.currentTimeMillis() / 1000 - 60);
        Log.e("xxxx", path + "");

        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                return null;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Entity data = adapter.getItem(position);
        if (data.clazz != null) {
            startActivity(new Intent(this, data.clazz));
        } else if (data.name.startsWith("/") ||
                data.name.startsWith("http://")) {
            Intent intent = new Intent(this, FFmpegActivity.class);
            intent.putExtra("uri", data.name);
            startActivity(intent);
        } else switch (data.name) {
            case "BrowsePicture":
                Intent intent = new Intent(this, PictureActivity.class);
                Callable.of(intent, arg -> {
                    mTextview.append(arg + " ");
                    return "MainActivity : " + arg;
                });
                startActivity(intent);
                break;
            case "ListenLocalPort":
                TestKit.localhost();
                break;
            case "WebViewActivity":
                startActivity(new Intent(this, WebViewActivity.class));
                break;
            case "AccessibilityService":
                ToolKit.openAccessibility(this);
                break;
            default:
                break;
        }
    }

    public void nativeInvokeHandle(String str) {
        AsyncRun.runOnUiThread(()-> Looper.myQueue()
                .addIdleHandler(()-> {
            adapter.add(new Entity(str));
            return false;
        }));
    }

    @Override
    public void finish() {
        super.finish();
        httpServer.stopServer();
        ActivityStack.exit();
        DeviceBandwidthSampler.getInstance().stopSampling();
    }

    static class Entity {
        public String name;
        public Class<?> clazz;

        public Entity(String name) {
            this.name = name;
        }

        public Entity(String name,
                      Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    private class ScanMediaTask extends AsyncTask<Void, Void, List<Entity>> {

        @Override
        protected List<Entity> doInBackground(Void... params) {
            List<Entity> list = new ArrayList<>();
            Cursor cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, null);
            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Video.Media.DATA));
                File file = new File(path);
                if (!file.exists()) {
                    continue;
                }
                list.add(new Entity(path));
            }
            cursor.close();
            return list;
        }

        @Override
        protected void onPostExecute(List<Entity> list) {
            list.add(new Entity("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"));
            adapter.addList(list);
        }
    }

    public static void newMethod() {
        Log.e("hot-fix","newMethod");
    }

    public native String initXhookFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}

