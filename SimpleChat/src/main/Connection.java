package main;

import main.helpers.JsonConverter;

import java.io.*;
import java.net.Socket;

public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private boolean closed;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void close() throws IOException {
        closed = true;
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public Message receiveMessage() throws IOException {
        Message message;
        synchronized (inputStream) {
            String incomingMessage = inputStream.readUTF();
            message = JsonConverter.convertFromJson(incomingMessage, Message.class);
            return message;
        }
    }

    public void sendMessage(Message message) {
        synchronized (outputStream) {
            try {
                outputStream.writeUTF(JsonConverter.convertToJson(message));
                outputStream.flush();
            } catch (IOException e) {
                System.out.println("Сообщение не было отправлено");
            }
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
