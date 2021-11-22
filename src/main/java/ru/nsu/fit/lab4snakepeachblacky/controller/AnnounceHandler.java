package ru.nsu.fit.lab4snakepeachblacky.controller;

import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.net.MsgWrap;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class AnnounceHandler {
    private Integer globalMsgSeq;
    private final MulticastSocket mcSocket;
    private final InetAddress group;
    public AnnounceHandler() throws IOException {
        mcSocket = new MulticastSocket(9192);
        group = InetAddress.getByName("239.192.0.4");
        mcSocket.joinGroup(group);
        globalMsgSeq = 0;
    }

    public void sendAnnounceMsg(SnakesProto.GamePlayers players, SnakesProto.GameConfig config) {
        try {
            var msg = SnakesProto.GameMessage.newBuilder()
                    .setAnnouncement(SnakesProto.GameMessage.AnnouncementMsg.newBuilder()
                            .setPlayers(players)
                            .setConfig(config).build())
                    .setMsgSeq(globalMsgSeq)
                    .build();
            var dataToSend = msg.toByteArray();
            mcSocket.send(new DatagramPacket(dataToSend, dataToSend.length, group, Constants.MULTI_PORT));
            globalMsgSeq += 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MsgWrap receiveAnnounce() {
        try {
            byte[] receivingBuffer = new byte[8192];
            DatagramPacket recvPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
            mcSocket.receive(recvPacket);
            byte[] receivedBytes = new byte[recvPacket.getLength()];
            System.arraycopy(receivingBuffer, 0, receivedBytes, 0, recvPacket.getLength());
            return new MsgWrap(recvPacket.getAddress().getHostAddress(),
                    SnakesProto.GameMessage.parseFrom(receivedBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
