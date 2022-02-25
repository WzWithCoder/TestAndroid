package com.ymt.server;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

/**
 * Created by wangzheng on 2016/9/5.
 */

public class HttpServer extends Thread {
    private Builder builder = null;
    private ServerSocket mServerSocket = null;
    private ExecutorService mThreadPool = null;
    private boolean isLoop = true;
    private Set<IRequestHandler> mRequestHandles = new HashSet<IRequestHandler>() {
        {
            add(new TestHandler());
        }
    };

    private HttpServer(Builder builder) {
        this.builder = builder;
        this.setDaemon(true);
        mThreadPool = Executors.newCachedThreadPool();
        if (builder.handles != null) {
            mRequestHandles.addAll(builder.handles);
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = ServerSocketFactory.getDefault()
                    .createServerSocket(builder.port, builder.maxParallels);
            while (isLoop) {
                final Socket remotePeer = mServerSocket.accept();
                mThreadPool.submit(new Runnable() {
                    public void run() {
                        onAcceptRemotePeer(remotePeer);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("HttpServer", e.getMessage());
        }
    }

    public void stopServer() {
        if (!isLoop) return;
        isLoop = false;
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Request-line {METHOD} {PATH} HTTP/{VERSION}CRLF
    //Response-line HTTP/{VERSION} {CODE} {MSG}CRLF
    //Header n {key: value}CRLF
    //CRLF
    //Request-body
    private void onAcceptRemotePeer(Socket socket) {
        try {
            RequestContext context = new RequestContext(socket);
            InputStream is = socket.getInputStream();
            String requestLine = StreamToolKit.readLine(is);
            UnpackToolKit.requestLine(requestLine, context);
            UnpackToolKit.headerLine(is, context);

//            String requestBody = StreamToolKit.readString(is);
//            context.addRequestParams(UnpackToolKit.formatParams(requestBody));
            for (IRequestHandler requestHandle : mRequestHandles) {
                if (requestHandle.accept(context.getUri())) {
                    requestHandle.handle(context);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private int port = 8088;
        private int maxParallels = 100;
        private Set<IRequestHandler> handles = null;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder maxParallels(int maxParallels) {
            this.maxParallels = maxParallels;
            return this;
        }

        public Builder registeHandler(IRequestHandler... handleList) {
            if (handles == null) {
                handles = new HashSet<IRequestHandler>();
            }
            if (handleList != null && handleList.length > 0) {
                handles.addAll(Arrays.asList(handleList));
            }
            return this;
        }

        public HttpServer build() {
            return new HttpServer(this);
        }
    }
}
