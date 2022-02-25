package com.example.wangzheng.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create by wangzheng on 2018/5/9
 */
public class BlockUploader extends Thread {
    private static final int BLOCK_SIZE = 1024 * 1024;
    private static final int CORE_POOL_SIZE = 5;

    private OkHttpClient mHttpClient;
    private ExecutorService mExecutorService;
    private volatile CountDownLatch mCountDownLatch;
    private RandomAccessFile mRandomAccessFile;
    private Set<Integer> mTaskBolcks;
    private int mRetryCount = 3;
    private int mTotalBlock/*, mPartIndex*/;

    public static BlockUploader from(File file) {
        return new BlockUploader(file);
    }

    private BlockUploader(File file) {
        try {
            mRandomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        mTotalBlock = (int) Math.ceil(file.length() * 1f / BLOCK_SIZE);
        mTaskBolcks = Collections.synchronizedSet(new HashSet<Integer>());
        mExecutorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
    }

    @Override
    public void run() {
        mUploadListener.onStart();
        boolean isNewTasker = mTaskBolcks == null || mTaskBolcks.isEmpty();
        mCountDownLatch = new CountDownLatch(isNewTasker ? mTotalBlock : mTaskBolcks.size());
        if (isNewTasker) {
            for (int index = 0; index < mTotalBlock; index++) {
                mExecutorService.submit(new Tasker(index));
            }
        } else {
            Integer[] indexArray = new Integer[mTaskBolcks.size()];
            mTaskBolcks.toArray(indexArray);
            mTaskBolcks.clear();
            for (Integer index : indexArray) {
                mExecutorService.submit(new Tasker(index));
            }
        }
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        complete();
    }

    private void complete() {
        mUploadListener.onComplete();
        if (mTaskBolcks == null || mTaskBolcks.isEmpty()) {
            mUploadListener.onSuccess("success");
        } else {
            mUploadListener.onFailed("error");
        }
    }

    class Tasker implements Runnable {
        private int index;
        private int executCount = 1;
        private boolean status;

        public Tasker(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            do {
                try {
                    if (status = process(index)) {
                        break;
                    }
                } catch (Throwable e) {
                    if (!retryHandler.canRetry(e, executCount)) {
                        e.printStackTrace();
                        break;
                    }
                }
            } while (executCount++ < mRetryCount);

            if (status) {
                progress();
            } else {
                mTaskBolcks.add(index);
            }

            mCountDownLatch.countDown();
        }

        private boolean process(int partIndex) throws IOException {
            byte[] data = readBlockByIndex(partIndex);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), data);
            Request.Builder builder = new Request.Builder()
                    .url("http://www.ymtou.com/upload")
                    .header("PartIndex", String.valueOf(partIndex))
                    .header("User-Agent", "android upload 1.0")
                    .put(requestBody);

            Call call = mHttpClient.newCall(builder.build());
            Response response = call.execute();
            if (response.isSuccessful()) {
                return true;
            } else {
                return false;
            }
        }

        private void progress() {
            float percent = 0;
            if (mTaskBolcks == null || mTaskBolcks.isEmpty()) {
                percent = (mTotalBlock - mCountDownLatch.getCount()) * 1f / mTotalBlock;
            } else {
                percent = (mTotalBlock - mCountDownLatch.getCount() - mTaskBolcks.size()) * 1f / mTotalBlock;
            }
            mUploadListener.onProgress(percent);
        }
    }

    private byte[] readBlockByIndex(int index) throws IOException {
        byte[] block = new byte[BLOCK_SIZE];
        int offset = index * BLOCK_SIZE;
        mRandomAccessFile.seek(offset);
        int readedSize = mRandomAccessFile.read(block, 0, BLOCK_SIZE);

        // read last block, adjust byte size
        if (readedSize < BLOCK_SIZE) {
            byte[] tempBlock = new byte[readedSize];
            System.arraycopy(block, 0, tempBlock, 0, readedSize);
            return tempBlock;
        }
        return block;
    }

    private RetryHandler retryHandler = new RetryHandler() {
        @Override
        public boolean canRetry(Throwable exception, int executCount) {
            if (executCount > mRetryCount) {
                return false;
            } else if (exception instanceof InterruptedIOException) {
                return false;
            } else if (exception instanceof ConnectException) {
                return false;
            } else if (exception instanceof OutOfMemoryError) {
                return false;
            }
            return true;
        }
    };

    public interface RetryHandler {
        boolean canRetry(Throwable exception, int executionCount);
    }

    private UploadListener mUploadListener;

    public BlockUploader setUploadListener(UploadListener uploadListener) {
        this.mUploadListener = uploadListener;
        return this;
    }

    public interface UploadListener<T> {
        void onStart();

        void onComplete();

        void onSuccess(T t);

        void onFailed(String msg);

        void onProgress(float percent);
    }
}
