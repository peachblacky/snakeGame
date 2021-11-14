package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class AckMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;

    public AckMsg(Integer seqNumber, String senderId, String receiverId) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = MessageType.ACK;
    }
}
