package com.example.wangzheng.http.common;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FileWrapper<T> implements Serializable {
    public T content;
    public String fileName;

    public FileWrapper(T content, String fileName) {
        this.content = content;
        this.fileName = fileName;
    }

    public FileWrapper(T content) {
        this.content = (T) content;
        if (content instanceof File) {
            this.fileName = ((File) content).getName();
        } else {
            this.fileName = System.currentTimeMillis() + "";
        }
    }

    public RequestBody getRequestBody() {
        final MediaType type = MediaType.parse("application/octet-stream");
        RequestBody requestBody = null;
        if (content instanceof InputStream) {
            byte[] bytes = Utils.toBytes((InputStream) content);
            requestBody = RequestBody.create(type, bytes);
        } else if (content instanceof File) {
            requestBody = RequestBody.create(type, (File) content);
        } else {
            throw new IllegalArgumentException("上传实体类型异常");
        }
        return requestBody;
    }

}