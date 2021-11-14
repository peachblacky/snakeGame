package ru.nsu.fit.lab4snakepeachblacky.net;

import ru.nsu.fit.lab4snakepeachblacky.model.GameConfig;

import java.io.Serializable;

public class AnnouncementMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private GameConfig config;
    private Integer playersNumber;
    private Boolean canJoin;

    public AnnouncementMsg(Integer seqNumber, String senderId, GameConfig config, Integer playersNumber) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.config = config;
        this.playersNumber = playersNumber;
        canJoin = playersNumber < 4;
        this.type = MessageType.ANNOUNCEMENT;
    }
}
