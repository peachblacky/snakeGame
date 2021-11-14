package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class JoinMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;
    private String name;

    public JoinMsg(Integer seqNumber, String senderId, String receiverId, String name) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.name = name;
        this.type = MessageType.JOIN;
    }
}
