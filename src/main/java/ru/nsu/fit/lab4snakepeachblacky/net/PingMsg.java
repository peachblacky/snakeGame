package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class PingMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;

    public PingMsg(Integer seqNumber, String senderId) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.type = MessageType.PING;
    }

    private String senderId;
}
