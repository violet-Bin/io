package com.bingo.io.bio.chat.server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: jiangjiabin
 * @date: Create in 1:04 2021/2/17
 * @description:
 */
public class ChatServer {

    private static final Integer DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";

    private ServerSocket serverSocket;

    private Map<Integer, Writer> connectedClients;

    private ExecutorService threadPool;

    public ChatServer() {
        threadPool = new ThreadPoolExecutor(5, 10, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50));
        connectedClients = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("server has started!");

            while (true) {
                //等待客户端连接
                Socket socket = serverSocket.accept();
                //创建ChatHandler线程
                System.out.println("1111111");
                threadPool.execute(new ChatHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closed();
        }

    }

    public void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(socket.getLocalPort(), writer);
            System.out.println("客户端[" + port +"]已连接到服务器");
        }
    }

    public void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.remove(port);
                connectedClients.get(port).close();
            }
            System.out.println("客户端["+ port +"]已断开连接");
        }
    }

    public void forwardMessage(Socket socket, String msg) {
        if (socket != null) {
            connectedClients.forEach((k, v) -> {
                if (!k.equals(socket.getPort())) {
                    try {
                        v.write(msg);
                        v.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public Boolean readyToQuit(Socket socket, String msg) {
        return StringUtils.equals(QUIT, msg);
    }

    private void closed() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println("server closed!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
