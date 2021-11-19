package ru.nsu.fit.lab4snakepeachblacky.model.net;

import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

public class MsgWrap {
    private String ip;
    private SnakesProto.GameMessage msg;

    public MsgWrap(String ip, SnakesProto.GameMessage msg) {
        this.ip = ip;
        this.msg = msg;
    }

    public String getIp() {
        return ip;
    }

    public SnakesProto.GameMessage getMsg() {
        return msg;
    }
}
