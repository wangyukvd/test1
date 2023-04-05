package com.example.test1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        //ServerSocket指定端口port
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //用线程池来做socket操作，这样就不用每次new Thread，会复用创建的Thread

        while (true) {
            Socket socket = serverSocket.accept();//阻塞到有连接访问，拿到socket
            System.out.println("get socket from:"+socket.hashCode());
            cachedThreadPool.execute(() -> {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();//拿到in out putStream，就想干啥干啥了
                    outputStream = socket.getOutputStream();
                    byte[] bytes = new byte[inputStream.available()];
                    int result = inputStream.read(bytes);//读取请求的所有内容，实质是好几行String，里面存有http信息
                    if (result != -1){
                        System.out.println(new String(bytes));
                    }

                    //也可用bufferreader readline，主要目的是拿到请求方法是GET POST PATCH还是其他http请求方式
                    //拿到path进行路由，看server端决定给他返回什么，这边可以封装一个router,携带一个由解析intputStream的来的自定义httprequest


                    String body = "<h1>hi</h1>";//可以是html文件，读文本文件进来就行了
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: " + body.getBytes().length + "\r\n" +
                            "Content-Type: text/html; charset-utf-8\r\n" +
                            "\r\n" +
                            body + "\r\n";
                    outputStream.write(response.getBytes());//按照协议，将返回请求由outputStream写入
                    outputStream.flush();
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();//异常捕获
                }

            });
        }
    }

}
