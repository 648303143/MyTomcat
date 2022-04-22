package com.example.mytomcatv2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zhangqingyang02
 * @date 2022-04-22-10:52
 */
public class Server {
    public static String WEB_ROOT = "/Users/a58/IdeaProjects/my-tomcat/src/main/resources/web";

    private static String sourceName = "";

    private static Map<String, String> map = new HashMap<>();

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/conf.properties"));
            Set<Object> set = properties.keySet();
            Iterator<Object> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String property = properties.getProperty(key);
                map.put(key, property);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;

        try {
            serverSocket = new ServerSocket(8082);
            while (true) {
                socket = serverSocket.accept();
                is = socket.getInputStream();
                os = socket.getOutputStream();
                parse(is);

                if (sourceName != null) {
                    if (sourceName.indexOf(".") != -1) {

                        sendStaticResource(os);
                    } else {
                        sendDynamicResource(is, os);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {

                os.close();

            }
            if (is != null) {
                is.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static void sendDynamicResource(InputStream is, OutputStream os) {

        try {
            os.write("HTTP/1.1 200 OK\n".getBytes());
            os.write("Server:apache-Coyote/1.1\n".getBytes());
            os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            os.write("\n".getBytes());

            if (map.containsKey(sourceName)) {
                Class<?> servletClass = Class.forName(map.get(sourceName));
                Servlet servlet = (Servlet)servletClass.newInstance();

                servlet.init();
                servlet.service(is,os);
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void sendStaticResource(OutputStream os) {
        FileInputStream fis = null;
        File file = new File(WEB_ROOT, sourceName);
        byte[] response = new byte[2048];
        try {
            if (file.exists()) {
                os.write("HTTP/1.1 200 OK\n".getBytes());
                os.write("Server:apache-Coyote/1.1\n".getBytes());
                os.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                os.write("\n".getBytes());

                fis = new FileInputStream(file);
                int len = -1;
                while ((len = fis.read(response)) != -1) {
                    os.write(response, 0, len);
                }
            } else {
                os.write("HTTP/1.1 404 not found\n".getBytes(StandardCharsets.UTF_8));
                os.write("Server:apache-Coyote/1.1\n".getBytes(StandardCharsets.UTF_8));
                os.write("Content-Type:text/html;charset=utf-8\n".getBytes(StandardCharsets.UTF_8));
                os.write("\n".getBytes());
                String errorMsg = "文件不存在";
                os.write(errorMsg.getBytes(StandardCharsets.UTF_8));
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void parse(InputStream is) throws IOException {
        StringBuilder questContent = new StringBuilder(2048);

        byte[] buffer = new byte[2048];
        int len = -1;

        len = is.read(buffer);

        for (int i = 0; i < len; i++) {
            questContent.append((char) buffer[i]);
        }
        System.out.println(questContent);
        parseUrl(questContent.toString());
    }

    private static void parseUrl(String questContent) {
        int index1 = questContent.indexOf(" ");
        if (index1 != -1) {
            int index2 = questContent.indexOf(" ", index1 + 1);
            if (index2 > index1) {
                sourceName = questContent.substring(index1 + 2, index2);
                System.out.println(sourceName);
            }
        }
    }
}
