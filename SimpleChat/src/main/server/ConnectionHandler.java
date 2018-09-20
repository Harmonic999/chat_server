package main.server;

import main.Connection;
import main.Message;
import main.MessageType;

import java.io.IOException;
import java.net.Socket;

import static main.MessageType.*;
import static main.server.ChatServer.*;

class ConnectionHandler extends Thread {

    private final Socket socket;

    ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Connection connection = new Connection(socket)) {
            registerNewConnectedUser(connection);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Соединение было разорвано");
            handleDisconnection();
        }
    }

    //проходимся по карте и удаляем из списка всех офлайн пользователей
    private void handleDisconnection() {
        onlineUsers.entrySet().removeIf(key -> key.getValue().isClosed());
    }

    @SuppressWarnings("all")
    private void registerNewConnectedUser(Connection connection) throws IOException, ClassNotFoundException {
        connection.sendMessage(new Message(NAME_REQUEST)); //запрашиваем имя у клиента
        Message message = connection.receiveMessage();
        String data = message.getData();

        if (message.getType() == USER_NAME
                && data != null
                && !data.isEmpty()
                && !onlineUsers.containsKey(data)) {
            System.out.println("Пользователь с именем " + data + " зарегистрирован");
            connection.sendMessage(new Message(NAME_ACCEPTED));
            onlineUsers.put(data, connection);
            Postman.sendMessageToAllOnlineUsers(new Message(data + " присоединился к чату.", MessageType.TEXT));
            chatLoop(data, connection); //Если имя валидное входим в основной цикл обработки сообщений
        } else if (message.getType() == USER_DISCONNECTED) {
            connection.close(); //Если пользователь вышел ещё до регистрации закрываем соединение
        } else {
            System.out.println("Пользователь с таким именем уже имеется или неправильный тип сообщения");
            connection.sendMessage(new Message(NAME_REJECTED));
        }
    }

    @SuppressWarnings("all")
    private void chatLoop(String userName, Connection connection) throws IOException, ClassNotFoundException {
        while (true) {
            Message message = connection.receiveMessage();

            switch (message.getType()) {
                case TEXT:
                    if (!Postman.messageIsPrivate(message.getData())) {
                        System.out.println(userName + ": " + message.getData());
                        Postman.sendMessageToAllOnlineUsers(message.appendUserName(userName));
                    } else {
                        Postman.sendPrivateMessage(userName, message);
                    }
                    break;
                case USER_DISCONNECTED:
                    onlineUsers.remove(userName);
                    System.out.println("Пользователь с именем " + userName + " покинул чат.");
                    Postman.sendMessageToAllOnlineUsers(new Message(userName + " покинул чат.", MessageType.TEXT));
                    break;
                default:
                    System.out.println("Несоответствующий тип сообщения");
            }
        }
    }
}
