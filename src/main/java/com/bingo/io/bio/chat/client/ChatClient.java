package com.bingo.io.bio.chat.client;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: jiangjiabin
 * @date: Create in 1:52 2021/2/17
 * @description:
 */
public class ChatClient {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final Integer DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private ExecutorService threadPool;

    public ChatClient() {
        threadPool = new ThreadPoolExecutor(2, 2, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50));
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }

    private void start() {
        try {
            socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //处理用户的输入
            threadPool.execute(new UserInputHandler(this));
            //读取服务器转发的消息
            String msg;
            while ((msg = receiveMsg()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void sendMsg(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    public String receiveMsg() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    public Boolean readyToQuit(String msg) {
        return StringUtils.equals(QUIT, msg);
    }

    private void close() {
        if (writer != null) {
            try {
                writer.close();
                System.out.println("已关闭socket！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
