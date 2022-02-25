package com.example.wangzheng.http.common;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Can decorate any request body, tracking the upload progress.
 * Created by xujian on 2016/6/27.
 */
public class ProgressRequestBody extends RequestBody {
    protected RequestBody delegate;
    protected ProgressListener progressListener;
    protected CountingSink countingSink;

    public ProgressRequestBody(RequestBody delegate, ProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            progressListener.onProgress(bytesWritten, contentLength());
        }
    }
}