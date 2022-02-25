package com.ymt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by wangzheng on 2016/10/13.
 */

public class TestHandler implements IRequestHandler {
    @Override
    public boolean accept(String uri) {
        return true;
    }

    @Override
    public void handle(RequestContext context) {
        try {
            OutputStream os = context.getSocket().getOutputStream();
            PrintWriter writer = new PrintWriter(os);
            writer.println(context.getProtocol() + " 200 OK");
           String response = "<html>" +
                            "<head>" +
                            "<title>端口监听</title>" +
                            "</head>" +
                            "<body>" +
                            "</br>" +
                            "</br>" +
                            "</br>" +
                            "<font size='30'><a href='http://127.0.0.2:8088/Activity'>打开app</a></font>" +
                            "</body>" +
                            "</html>";
            writer.println("Content-Length:" + response.getBytes().length);
            writer.println("Content-Type:text/html;charset=utf-8");
            writer.println();
            writer.println(response);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
