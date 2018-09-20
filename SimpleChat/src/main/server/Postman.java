package main.server;

import main.Connection;
import main.Message;

import static main.server.ChatServer.onlineUsers;

class Postman {

    static boolean messageIsPrivate(String message) {
        return message.startsWith("/");
    }

    static void sendMessageToAllOnlineUsers(Message message) {
        onlineUsers.forEach((userName, connection) -> connection.sendMessage(message));
    }

    static void sendPrivateMessage(String senderName, Message message) {
        String receiverName = getReceiverName(message.getData());

        Connection receiverConnection = onlineUsers.get(receiverName);
        Connection senderConnection = onlineUsers.get(senderName);

        if (receiverConnection != null && !senderName.equals(receiverName)) { //Самому себе сообщения не отправляем
            message.cutReceiverName().addPrivatePrefixWithSenderName(senderName);
            receiverConnection.sendMessage(message);
            senderConnection.sendMessage(message);
        }
    }

    private static String getReceiverName(String message) { //Личные сообщения отправляются через слэш + имя получателя
        String[] words = message.split(" ");
        return words[0].replaceFirst("/", "");
    }

}
