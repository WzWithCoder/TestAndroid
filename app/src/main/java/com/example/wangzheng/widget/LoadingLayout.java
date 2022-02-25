package com.example.wangzheng.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wangzheng.R;

/**
 * 页面加载动画管理器
 * Created by wangzheng on 2015/7/16.
 */
public class LoadingLayout extends FrameLayout implements View.OnClickListener {
    private View loadingPage = null;
    private View emptyPage   = null;
    private View failedPage  = null;
    private View contentView = null;

    private TextView  failedTextView = null;
    private TextView  emptyTextView  = null;
    private ImageView emptyImageView = null;
    private View      retryButton    = null;

    private OnClickListener onRetryClickListener = null;


    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LoadingLayout, defStyleAttr, 0);

        int loadingPageId = typedArray.getResourceId(
                R.styleable.LoadingLayout_loading_page_layout,
                R.layout.default_loading_layout);
        loadingPage = inflate(context, loadingPageId, null);
        int failedPageId = typedArray.getResourceId(
                R.styleable.LoadingLayout_failed_page_layout,
                R.layout.default_failed_layout);
        failedPage = inflate(context, failedPageId, null);
        int emptyPageId = typedArray.getResourceId(
                R.styleable.LoadingLayout_empty_page_layout,
                R.layout.default_empty_layout);
        emptyPage = inflate(context, emptyPageId, null);

        int emptyTextId = typedArray.getResourceId(
                R.styleable.LoadingLayout_empty_text_id,
                R.id.empty_text_id);
        emptyTextView = findView(emptyPage, emptyTextId);
        int emptyImageId = typedArray.getResourceId(
                R.styleable.LoadingLayout_empty_image_id,
                R.id.empty_image_id);
        emptyImageView = findView(emptyPage, emptyImageId);
        int failedTextId = typedArray.getResourceId(
                R.styleable.LoadingLayout_failed_text_id,
                R.id.failed_text_id);
        failedTextView = findView(failedPage, failedTextId);
        int retryButtonId = typedArray.getResourceId(
                R.styleable.LoadingLayout_retry_button_id,
                R.id.retry_button_id);
        retryButton = findView(failedPage, retryButtonId);

        emptyPage.setOnClickListener(this);

        if (retryButton == null) {
            failedPage.setOnClickListener(this);
        } else {
            retryButton.setOnClickListener(this);
        }

        typedArray.recycle();

        addView(failedPage);
        addView(loadingPage);
        addView(emptyPage);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        //防止页面穿透
        if (!contentView.hasOnClickListeners()) {
            contentView.setClickable(false);
        }
        if (!failedPage.hasOnClickListeners()) {
            failedPage.setClickable(false);
        }
        if (!loadingPage.hasOnClickListeners()) {
            loadingPage.setClickable(false);
        }
        if (!emptyPage.hasOnClickListeners()) {
            emptyPage.setClickable(false);
        }
    }

    public LoadingLayout showContent() {
        loadingPage.setVisibility(View.GONE);
        failedPage.setVisibility(View.GONE);
        emptyPage.setVisibility(View.GONE);
        return this;
    }

    public LoadingLayout showLoading() {
        loadingPage.setVisibility(View.VISIBLE);
        loadingPage.bringToFront();
        failedPage.setVisibility(View.GONE);
        emptyPage.setVisibility(View.GONE);
        return this;
    }

    public LoadingLayout showEmpty() {
        emptyPage.setVisibility(View.VISIBLE);
        emptyPage.bringToFront();
        failedPage.setVisibility(View.GONE);
        loadingPage.setVisibility(View.GONE);
        return this;
    }

    public LoadingLayout showFailed(String failedMsg) {
        failedPage.setVisibility(View.VISIBLE);
        failedPage.bringToFront();
        emptyPage.setVisibility(View.GONE);
        loadingPage.setVisibility(View.GONE);
        if (failedTextView != null &&
                !TextUtils.isEmpty(failedMsg)) {
            failedTextView.setText(failedMsg);
        }
        return this;
    }

    public LoadingLayout setEmptyText(String text) {
        if (emptyTextView != null && text != null) {
            emptyTextView.setText(text);
        }
        return this;
    }

    public LoadingLayout setEmptyImage(@DrawableRes int resId) {
        if (emptyImageView != null)
            emptyImageView.setBackgroundResource(resId);
        return this;
    }

    public LoadingLayout setOnRetryClickListener(
            OnClickListener onRetryClickListener) {
        this.onRetryClickListener = onRetryClickListener;
        return this;
    }

    public <T extends View> T findView(View rootView, int id) {
        return (T) rootView.findViewById(id);
    }

    @Override
    public void onClick(View v) {
        if (onRetryClickListener != null) {
            showLoading();
            onRetryClickListener.onClick(v);
        }
    }
}
