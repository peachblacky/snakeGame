package ru.nsu.fit.lab4snakepeachblacky.net;

import java.io.Serializable;

public class RoleChangeMsg implements Message, Serializable {
    private MessageType type;
    private Integer seqNumber;
    private String senderId;
    private String receiverId;
    private PlayerRole senderRole;
    private PlayerRole receiverRole;

    public RoleChangeMsg(Integer seqNumber, String senderId, String receiverId, PlayerRole senderRole, PlayerRole receiverRole) {
        this.seqNumber = seqNumber;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderRole = senderRole;
        this.receiverRole = receiverRole;
        this.type = MessageType.ROLE;
    }
}
