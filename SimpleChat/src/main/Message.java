package main;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect
public class Message implements Serializable {

    private String data;
    private MessageType type;

    public Message(String data, MessageType type) {
        this.data = data;
        this.type = type;
    }

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    //конструктор для Jackson
    public Message() {
    }

    public String getData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }

    public Message appendUserName(String userName) {
        data = userName + ": " + data;
        return this;
    }

    public Message cutReceiverName() {
        int firstSpace = data.indexOf(" ");
        data = data.substring(firstSpace, data.length());
        return this;
    }

    public Message addPrivatePrefixWithSenderName(String userName) {
        data = userName + " шепчет: " + data;
        return this;
    }
}
