package com.example.wangzheng.common;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;


/**
 * Create by wangzheng on 2018/10/24
 */
public abstract class LazyFragment extends Fragment {
    //Fragment的View绑定完毕的标记
    protected boolean isBindView;
    //Fragment对用户可见的标记
    protected boolean isVisibleToUser;
    //0:不加载；1:加载一次；2:切换刷新；
    protected short isOnceFlag = 1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    private void bindView(View view) {
        onBindView(view);
        isBindView = true;
    }

    private void lazyLoad() {
        if (isBindView && isVisibleToUser
                && isOnceFlag > 0) {
            if (isOnceFlag == 1) {
                isOnceFlag = 0;
            }
            onLoadData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isBindView = false;
        isVisibleToUser = false;
        isOnceFlag = 1;
    }

    public abstract void onBindView(View view);

    public abstract void onLoadData();
}
