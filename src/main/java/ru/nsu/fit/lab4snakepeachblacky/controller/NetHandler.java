package ru.nsu.fit.lab4snakepeachblacky.controller;

import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.io.*;
import java.net.*;
//import ru.nsu.fit.lab4snakepeachblacky.model;

public class NetHandler {



    private Integer globalMsgSeq;
//    private PlayerRole playerRole;
//    private final MulticastSocket mcSocket;
    private final DatagramSocket ucSocket;
//    private final InetAddress group;
//    private final InetSocketAddress mcGroupAddr;
//    private final DatagramChannel dc;
//    private final MembershipKey mcKey;

    public NetHandler() throws IOException {
        globalMsgSeq = 0;
        ucSocket = new DatagramSocket(Constants.UNI_PORT);
//        mcSocket = new MulticastSocket(9192);
//        group = InetAddress.getByName("239.192.0.4");
//        mcSocket.joinGroup(group);
    }


    public synchronized void sendUnicastMsg(SnakesProto.GameMessage msg, String receiverId) {
        try {
            InetAddress addrToSend = InetAddress.getByName(receiverId);
            var dgToSend = new DatagramPacket(msg.toByteArray(), msg.getSerializedSize(), addrToSend, Constants.UNI_PORT);
            ucSocket.send(dgToSend);
            globalMsgSeq += 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
