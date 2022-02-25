package com.example.wangzheng.http.common;

public interface ProgressListener {
    void onProgress(long bytesWritten, long contentLength);
}