package com.example.wangzheng.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.wangzheng.R;
import com.example.wangzheng.common.NativeLib;
import com.example.wangzheng.status_bar.StatusBar;

public class FFmpegActivity extends AppCompatActivity {

    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBar.setTranslucent(this,true);

        uri = getIntent().getStringExtra("uri");
        NativeLib.setDataSource(uri);
        //final TextureView textureView = findViewById(R.id.textureView); //与EGL有冲突
        final SurfaceView surfaceView = findViewById(R.id.surfaceView);
        final CheckBox switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NativeLib.play(uri);
                } else {
                    NativeLib.pause(uri);
                }
            }
        });

        //region SurfaceView 背景透明
        //1.surfaceView.setZOrderOnTop(true);
        //2.holder.setFormat(PixelFormat.TRANSLUCENT);
        //3.Activity 必须使用透明主题
        //endregion
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                NativeLib.createSurface(uri, holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                NativeLib.createSurface(holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                NativeLib.destorySurface(uri);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        NativeLib.pause(uri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NativeLib.play(uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NativeLib.release(uri);
    }
}
