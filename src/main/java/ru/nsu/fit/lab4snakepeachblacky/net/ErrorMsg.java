package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class ErrorMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;
    private String errorMessage;

    public ErrorMsg(Integer seqNumber, String senderId, String receiverId) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = MessageType.ERROR;
    }
}
