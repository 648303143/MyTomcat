package com.example.mytomcatv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhangqingyang02
 * @date 2022-04-22-17:08
 */
public class bbServlet implements Servlet{
    @Override
    public void init() {
        System.out.println("bbServlet ----- init");
    }

    @Override
    public void service(InputStream is, OutputStream os) throws IOException {
        System.out.println("bbServlet ----- service");
        os.write("bbServlet!!!!!!".getBytes());
        os.flush();
    }

    @Override
    public void destroy() {
        System.out.println("bbServlet ----- destroy");

    }
}
