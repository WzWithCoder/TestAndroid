package com.example.wangzheng.common;

/**
 * Created by wangzheng on 2016/9/21.
 * 频率控制类，用于控制一定时间内的触发次数不能过多
 */

public class FrequeController {
    private int  mCounts           = 0;//设定时间内允许触发的次数
    private int  mSeconds          = 0;//设定的时间秒数
    private int  mCurrentCounts    = 0;//当前已经触发的次数
    private long mFirstTriggerTime = 0;//当前时间段内首次触发的时间

    public FrequeController(int counts, int seconds) {
        this.mCounts = counts;
        this.mSeconds = seconds;
        this.mCurrentCounts = 0;
        this.mFirstTriggerTime = 0;
    }

    public boolean canTrigger() {
        long currentTime = System.currentTimeMillis();

        //重置首次触发时间和已经触发次数
        if (mFirstTriggerTime == 0 || currentTime - mFirstTriggerTime > 1000 * mSeconds) {
            mFirstTriggerTime = currentTime;
            mCurrentCounts = 0;
        }

        //已经触发了mCounts次，本次不能触发
        if (mCurrentCounts >= mCounts) {
            return false;
        }

        ++mCurrentCounts;
        return true;
    }
}
