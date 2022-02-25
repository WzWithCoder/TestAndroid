package com.example.wangzheng.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ViewFlipper;

import com.example.wangzheng.R;


/**
 * Created by wangzheng on 16/5/31.
 */
public class MarqueView extends ViewFlipper {

    private int     mAnimDuration = 0;
    private Adapter mAdapter      = null;

    public MarqueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeViewStyle, defStyleAttr, 0);
        mAnimDuration = typedArray.getInteger(R.styleable.MarqueeViewStyle_AnimDuration, 500);
        typedArray.recycle();

        Animation animIn = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_in);
        animIn.setDuration(mAnimDuration);
        setInAnimation(animIn);

        Animation animOut = AnimationUtils.loadAnimation(context, R.anim.anim_marquee_out);
        animOut.setDuration(mAnimDuration);
        setOutAnimation(animOut);
    }

    public boolean start() {
        if (mAdapter == null || mAdapter.getCount() <= 1) return false;
        startFlipping();
        return true;
    }

    public MarqueView setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(mObserver);
        return this;
    }

    private DataSetObserver mObserver = new DataSetObserver(){
        public void onChanged() {
            MarqueView.this.removeAllViews();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                addView(mAdapter.getView(i, null, MarqueView.this));
            }
        }
        public void onInvalidated() {
            MarqueView.this.removeAllViews();
            super.onInvalidated();
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
    }
}
