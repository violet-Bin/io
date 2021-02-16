package com.bingo.io.bio.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author: jiangjiabin
 * @date: Create in 1:27 2021/2/17
 * @description:
 */
public class ChatHandler implements Runnable {

    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String msg;
            while ((msg = reader.readLine()) != null) {
                String fwdMsg = "客户端[" + socket.getPort() + "]: " + msg + "\n";
                System.out.println(fwdMsg);
                chatServer.forwardMessage(socket, fwdMsg);
                if (chatServer.readyToQuit(socket, msg)) {
                    System.out.println("客户端[" + socket.getPort() + "]: " + "已断开连接");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
