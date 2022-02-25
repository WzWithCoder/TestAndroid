package com.ymt.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzheng on 2016/9/5.
 */

public class RequestContext {
    private Socket socket = null;
    private String method = null;
    private String protocol = null;
    private String uri;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public RequestContext(Socket socket) {
        this.socket = socket;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void addRequestHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addRequestParam(String key, String value) {
        params.put(key, value);
    }

    public void addRequestParams(Map<String, String> subParams) {
        params.putAll(subParams);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Socket getSocket() {
        return socket;
    }
}
