package main.server;

import main.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 8080;
    static final Map<String, Connection> onlineUsers = new ConcurrentHashMap<>();

    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Устанавливается соединение...");
                ConnectionHandler handler = new ConnectionHandler(socket);
                handler.start();
            }
        }
    }
}
