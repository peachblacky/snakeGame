package ru.nsu.fit.lab4snakepeachblacky.controller;

import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.net.MsgWrap;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.io.*;
import java.net.*;

public class NetHandler {


    private final DatagramSocket uniSocket;

    public NetHandler() throws IOException {
        uniSocket = new DatagramSocket(Constants.UNI_PORT);
    }


    public synchronized void sendUnicastMsg(SnakesProto.GameMessage msg, String receiverId) {
        try {
            InetAddress addrToSend = InetAddress.getByName(receiverId);
            var dgToSend = new DatagramPacket(msg.toByteArray(), msg.getSerializedSize(), addrToSend, Constants.UNI_PORT);
            uniSocket.send(dgToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private synchronized SnakesProto.GameMessage receiveUnicastMag() {
//        return getGameMessage(uniSocket);
//    }

    public synchronized MsgWrap receiveUnicastMsg() {
        try {
            byte[] receivingBuffer = new byte[8192];
            DatagramPacket recvPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
            uniSocket.receive(recvPacket);
            byte[] receivedBytes = new byte[recvPacket.getLength()];
            System.arraycopy(receivingBuffer, 0, receivedBytes, 0, recvPacket.getLength());
            return new MsgWrap(recvPacket.getAddress().getHostAddress(), SnakesProto.GameMessage.parseFrom(receivedBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
