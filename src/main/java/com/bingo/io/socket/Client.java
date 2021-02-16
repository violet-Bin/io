package com.bingo.io.socket;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;

/**
 * @author: jiangjiabin
 * @date: Create in 21:42 2021/2/16
 * @description:
 */
public class Client {

    public static void main(String[] args) {
        final String DEFAULT_IP = "127.0.0.1";
        final Integer DEFAULT_PORT = 8888;
        final String QUIT = "quit";
        Socket socket = null;
        BufferedWriter writer = null;

        try {
            socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            //创建IO流
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {

                //等待用户输入信息
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String input = consoleReader.readLine();
                //发送msg
                writer.write(input + "\n");
                writer.flush();
                //读取从服务器收到的消息
                String msg = reader.readLine();
                System.out.println(msg);
                if (StringUtils.equals(input, QUIT)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    System.out.println("关闭client socket!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
