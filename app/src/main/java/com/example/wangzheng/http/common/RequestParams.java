package com.example.wangzheng.http.common;

import android.text.TextUtils;

import com.example.wangzheng.common.Md5Kit;
import com.example.wangzheng.http.callback.AbsCallBack;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by wangzheng on 2016/8/17.
 */
public class RequestParams {

    protected final ConcurrentHashMap<String, String> params;
    protected final ConcurrentHashMap<String, RequestParams.StreamWrapper> streamParams;
    protected final ConcurrentHashMap<String, RequestParams.FileWrapper> fileParams;

    public RequestParams() {
        this.params = new ConcurrentHashMap();
        this.streamParams = new ConcurrentHashMap();
        this.fileParams = new ConcurrentHashMap();
    }

    public void put(String key, String value) {
        params.put(key, value);
    }

    public void put(String key, String fileName, File file) {
        put(key, new FileWrapper(file, fileName));
    }

    public void put(String key, File file) {
        put(key, new FileWrapper(file));
    }

    public void put(String key, String fileName, InputStream is) {
        put(key, new StreamWrapper(is, fileName));
    }

    public void put(String key, StreamWrapper streamWrapper) {
        streamParams.put(key, streamWrapper);
    }

    public void put(String key, FileWrapper fileWrapper) {
        fileParams.put(key, fileWrapper);
    }


    private MultipartBody.Builder createMultipartBody(final AbsCallBack callback) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        Iterator<Map.Entry<String, StreamWrapper>> iteratorStream = streamParams.entrySet().iterator();
        while (iteratorStream.hasNext()) {
            Map.Entry<String, StreamWrapper> entry = iteratorStream.next();
            StreamWrapper streamWrapper = entry.getValue();
            RequestBody fileBody = streamWrapper.getRequestBody();
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, new ProgressListener() {
                public void onProgress(final long bytesWritten, final long contentLength) {
                    callback.sendProgressMessage((int) (bytesWritten * 100 / contentLength));
                }
            });
            bodyBuilder.addFormDataPart(entry.getKey(), streamWrapper.fileName, progressRequestBody);
        }
        Iterator<Map.Entry<String, FileWrapper>> iteratorFile = fileParams.entrySet().iterator();
        while (iteratorFile.hasNext()) {
            Map.Entry<String, FileWrapper> entry = iteratorFile.next();
            FileWrapper fileWrapper = entry.getValue();
            RequestBody fileBody = fileWrapper.getRequestBody();
            ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, new ProgressListener() {
                public void onProgress(final long bytesWritten, final long contentLength) {
                    callback.sendProgressMessage((int) (bytesWritten * 100 / contentLength));
                }
            });
            bodyBuilder.addFormDataPart(entry.getKey(), fileWrapper.fileName, progressRequestBody);
        }
        return bodyBuilder;
    }

    private void buildMultipartBody(MultipartBody.Builder builder, Map.Entry<String, Object> entry, final AbsCallBack callback) {
        RequestBody fileBody = null;
        String fileName = null;
        if (entry.getValue() instanceof InputStream) {
            byte[] bytes = Utils.toBytes((InputStream) entry.getValue());
            fileBody = RequestBody.create(type, bytes);
            fileName = Md5Kit.md5(bytes);
        } else if (entry.getValue() instanceof File) {
            File file = (File) entry.getValue();
            fileBody = RequestBody.create(type, file);
            fileName = Md5Kit.md5(file);
        }
        fileName = "md5-" + (TextUtils.isEmpty(fileName) ? System.currentTimeMillis() : fileName) + ".jpg";
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, new ProgressListener() {
            public void onProgress(final long bytesWritten, final long contentLength) {
                callback.sendProgressMessage((int) (bytesWritten * 100 / contentLength));
            }
        });
        builder.addFormDataPart(entry.getKey(), fileName, progressRequestBody);
    }


    public class StreamWrapper implements Serializable {
        public InputStream inputStream;
        public String fileName;
        public boolean isExtractFeature;

        public StreamWrapper(InputStream inputStream, String fileName, boolean isExtractFeature) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.isExtractFeature = isExtractFeature;
        }

        public StreamWrapper(InputStream inputStream, String fileName) {
            this(inputStream,fileName,false);
        }

        public RequestBody getRequestBody() {
            byte[] bytes = Utils.toBytes(inputStream);
            RequestBody requestBody = RequestBody.create(type, bytes);
            return requestBody;
        }
    }

    public class FileWrapper implements Serializable {
        public File file;
        public String fileName;
        public boolean isExtractFeature;

        public FileWrapper(File file, String fileName, boolean isExtractFeature) {
            this.file = file;
            this.fileName = fileName;
            this.isExtractFeature = isExtractFeature;
        }

        public FileWrapper(File file, String fileName) {
            this(file,fileName,false);
        }

        public FileWrapper(File file) {
            this.file = file;
            this.fileName = file.getName();
        }

        public RequestBody getRequestBody() {
            RequestBody requestBody = RequestBody.create(type, file);
            return requestBody;
        }
    }

    final MediaType type = MediaType.parse("application/octet-stream");
}
