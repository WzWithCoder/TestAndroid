package com.example.wangzheng.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * A collection of utilities for encoding URLs.
 * <p/>
 * Created by wangzheng on 2016/7/8.
 */
public class URLCoder {
    /**
     * Returns a String that is suitable for use as an <code>application/x-www-form-urlencoded
     * list of parameters in an HTTP PUT or HTTP POST.
     *
     * @param parameters The parameters to include.
     * @param encoding   The encoding to use.
     */
    public static String format(
            final Map<String, String> parameters, final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            final String encodedName = encode(parameter.getKey(), encoding);
            final String value = parameter.getValue();
            final String encodedValue = value != null ? encode(value, encoding) : "";
            if (result.length() > 0)
                result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
        }
        return result.toString();
    }

    public static String format(String url, Map<String, String> params) {
        if (url == null) return null;
        if (params == null || params.isEmpty()) return url;
        return url + (url.lastIndexOf('?') == -1 ? "?" : "&")
                + format(params, UTF_8);
    }

    private static String decode(final String content, final String encoding) {
        try {
            return URLDecoder.decode(content,
                    encoding != null ? encoding : DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                    encoding != null ? encoding : DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    private static final String PARAMETER_SEPARATOR     = "&";
    private static final String NAME_VALUE_SEPARATOR    = "=";
    public static final  String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    public static final  String USER_AGENT              = "User-Agent";
    public static final  String UTF_8                   = "UTF-8";
}