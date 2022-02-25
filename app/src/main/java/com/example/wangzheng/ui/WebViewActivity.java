package com.example.wangzheng.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;

import com.example.wangzheng.R;
import com.example.wangzheng.status_bar.StatusBar;

public class WebViewActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_layout);
        StatusBar.setLightMode(this,0xFFFFFFFF);

        mWebView = findViewById(R.id.webView);
        String response = "<html>" +
                "<head>" +
                "<title>端口监听</title>" +
                "</head>" +
                "<body>" +
                "<font size='35'>端口监听</font>" +
                "</br>" +
                "</br>" +
                "</br>" +
                "<font size='30'><a href='content://www.ipc.remote/23'>打开app</a></font>" +
                "</hr>" +
                "<img src='content://www.ipc.remote/24'/>" +
                "</body>" +
                "</html>";
//        mWebView.loadData(response,"text/html", "UTF-8");
//        mWebView.loadUrl("content://www.ipc.remote/local.html");
//        mWebView.loadUrl("http://127.0.0.2:8088");
        mWebView.loadUrl("https://95508.com/zcYyHXXo");
    }

    private String url = "content://www.ipc.remote";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(mWebView.canGoBack()) {
                //获取webView的浏览记录
                WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
                //这里的判断是为了让页面在有上一个页面的情况下，跳转到上一个html页面，而不是退出当前activity
                if (mWebBackForwardList.getCurrentIndex() > 0) {
                    String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
                    if (!historyUrl.equals(url)) {
                        mWebView.goBack();
                        return true;
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
