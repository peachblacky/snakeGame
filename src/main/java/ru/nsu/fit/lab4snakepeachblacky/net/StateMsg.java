package ru.nsu.fit.lab4snakepeachblacky.net;

import ru.nsu.fit.lab4snakepeachblacky.model.GameConfig;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Grid;

import java.io.Serializable;

public class StateMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;
    private Grid gameState;

    public StateMsg(Integer seqNumber, String senderId, String receiverId, Grid gameState) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.gameState = gameState;
        this.type = MessageType.STATE;
    }

}
