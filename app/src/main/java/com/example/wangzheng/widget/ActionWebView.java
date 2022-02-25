package com.example.wangzheng.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wangzheng.common.CommonKit;

import java.util.ArrayList;

//https://www.jianshu.com/p/a6f7b391a0b8
public class ActionWebView extends WebView {

    private ArrayList<String> mActions = new ArrayList<>();

    public ActionWebView(Context context) {
        this(context, null);
    }

    public ActionWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WebSettings webSettings = getSettings();

        //region 设置自适应屏幕
        webSettings.setUseWideViewPort(true);      //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); //缩放至屏幕的大小
        //endregion

        //region 缩放操作
        webSettings.setSupportZoom(true);          //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true);  //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //endregion

        //region 禁用 file:// 协议；防止文件泄密
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //endregion

        //支持Javascript交互
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        //允许自动播放视频
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        //5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //网页中触发下载动作
        setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                CommonKit.toast("onDownloadStart:" + url);
            }
        });

        //js交互接口
        addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void callback(final String value, final String title) {
                CommonKit.toast(title + "-" + value);
            }
        }, "JsInterface");


        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //拦截页面中的url加载,21以下的
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //拦截页面中的url加载,21以上的
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载完成
                super.onPageFinished(view, url);
            }


            @Override
            public void onLoadResource(WebView view, String url) {
                //页面加载每一个资源，如图片
                super.onLoadResource(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                //监听WebView发出的请求并做相应的处理
                //浏览器的渲染以及资源加载都是在一个线程中，如果在shouldInterceptRequest处理时间过长，WebView界面就会阻塞
                //21以下的
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                //监听WebView发出的请求并做相应的处理
                //浏览器的渲染以及资源加载都是在一个线程中，如果在shouldInterceptRequest处理时间过长，WebView界面就会阻塞
                //21以上的
                return super.shouldInterceptRequest(view, request);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //页面加载出现错误,23以下的
                switch (errorCode) {
                    case 404:
                        //view.loadUrl("加载一个错误页面提示，优化体验");
                        break;
                }
            }

            @TargetApi(23)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //页面加载出现错误,23以上的
                switch (error.getErrorCode()) {
                    case 404:
                        //view.loadUrl("加载一个错误页面提示，优化体验");
                        break;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //https错误 忽略
                handler.proceed();
            }
        });


        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //页面加载进度
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取标题
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                //获取图标
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //是否支持页面中的js警告弹出框
                CommonKit.toast(message);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                //是否支持页面中的js确定弹出框
                CommonKit.toast(message);
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //是否支持页面中的js输入弹出框
                CommonKit.toast(message);
                return super.onJsPrompt(view, url, message, defaultValue, result);

            }
        });


        mActions.add("custom test");
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        ActionMode actionMode = super.startActionMode(callback);
        return resolveActionMode(actionMode);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        ActionMode actionMode = super.startActionMode(callback, type);
        return resolveActionMode(actionMode);
    }

    private ActionMode resolveActionMode(final ActionMode actionMode) {
        if (actionMode == null) return null;
        MenuItem.OnMenuItemClickListener itemClickListener =
                new MenuItem.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        getSelectedData((String) item.getTitle());
                        actionMode.finish();
                        return true;
                    }
                };
        Menu menu = actionMode.getMenu();
        for (String action : mActions) {
            menu.add(action).setOnMenuItemClickListener(itemClickListener);
        }
        return actionMode;
    }

    private void getSelectedData(String id) {
        String js = "(function getSelectedText() {" +
                "var txt;" +
                "var id = \"" + id + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "JsInterface.callback(txt,id);" +
                "})()";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, new ValueCallback<String>() {
                public void onReceiveValue(String value) {
                    Log.i(getClass().getSimpleName(), value);
                }
            });
        } else {
            loadUrl("javascript:" + js);
        }
    }
}