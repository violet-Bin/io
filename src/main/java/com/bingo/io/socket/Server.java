package com.bingo.io.socket;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: jiangjiabin
 * @date: Create in 21:28 2021/2/16
 * @description:
 *
 */
public class Server {

    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务端，监听端口: " + DEFAULT_PORT);
            String msg = null;
            while (true) {
                //等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端[" + socket.getPort() + "]已连接");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //读取
                while ((msg = reader.readLine()) != null) {
                    System.out.println("客户端[" + socket.getPort() + "]: " + msg);
                    //回复
                    writer.write("服务器回复 [" + msg + "]\n");
                    writer.flush();

                    if (StringUtils.equals(QUIT, msg)) {
                        System.out.println("客户端[" + socket.getPort() + "]: " + "已断开连接");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("关闭server socketServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
