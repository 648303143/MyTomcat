package com.example.mytomcatv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhangqingyang02
 * @date 2022-04-22-17:03
 */
public interface Servlet {
    void init();
    void service(InputStream is, OutputStream os) throws IOException;
    void destroy();
}
