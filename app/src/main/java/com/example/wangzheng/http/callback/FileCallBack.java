package com.example.wangzheng.http.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class FileCallBack extends AbsCallBack<File> {
    private File file = null;

    public FileCallBack(String path) {
        file = new File(path);
        file.getParentFile().mkdirs();
    }

    @Override
    public void onResponse(Response response) throws Exception {
        File file = saveFile(response);
        sendSuccessMessage(file);
    }

    public File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long progress = 0;
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                progress += len;
                sendProgressMessage((int) (progress * 100f / total));
            }
            fos.flush();
            return file;
        } finally {
            try {
                response.body().close();
                if (is != null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
