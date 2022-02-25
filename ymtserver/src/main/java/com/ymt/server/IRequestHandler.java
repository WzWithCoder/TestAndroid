package com.ymt.server;

/**
 * Created by wangzheng on 2016/9/5.
 */

public interface IRequestHandler {
    boolean accept(String uri);

    void handle(RequestContext context);
}
