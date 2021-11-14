package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class SteerMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;
    private Integer direction;

    public SteerMsg(Integer seqNumber, String senderId, String receiverId, Integer direction) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.direction = direction;
        this.type = MessageType.STEER;
    }
}
