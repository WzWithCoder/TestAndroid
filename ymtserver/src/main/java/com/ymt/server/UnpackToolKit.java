package com.ymt.server;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzheng on 2016/9/5.
 */

public class UnpackToolKit {
    public static void requestLine(String requestLine, RequestContext context) throws IOException {
        String[] linePack = requestLine.split(" ");
        context.setMethod(linePack[0]);
        context.setProtocol(StreamToolKit.clearCRLF(linePack[2]));
        String uri = linePack[1];
        String[] uriPack = uri.split("\\?");

        if (uriPack.length > 1) {
            String paramPack = uriPack[1];
            Map<String, String> params = formatParams(paramPack);
            context.addRequestParams(params);
            context.setUri(uriPack[0]);
        } else {
            context.setUri(uri);
        }
    }

    public static void headerLine(InputStream is, RequestContext context) throws IOException {
        String headerLine = null;
        while ((headerLine = StreamToolKit.readLine(is)) != null) {
            if (headerLine.equals("\r\n")) break;
            String[] headerPair = headerLine.split(": ");
            context.addRequestHeader(headerPair[0], headerPair[1]);
        }
    }

    public static Map<String, String> formatParams(String paramPack) {
        if(TextUtils.isEmpty(paramPack))return null;
        String[] pairs = paramPack.split("&");
        if (pairs == null || pairs.length == 0) return null;
        Map<String, String> params = new HashMap<>();
        for (String pair : pairs) {
            String[] entry = pair.split("=");
            params.put(entry[0], entry[1]);
        }
        return params;
    }
}
