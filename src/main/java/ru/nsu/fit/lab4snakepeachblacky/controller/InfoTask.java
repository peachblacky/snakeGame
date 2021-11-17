//package ru.nsu.fit.lab4snakepeachblacky.controller;
//
//import javafx.scene.layout.AnchorPane;
//import ru.nsu.fit.lab4snakepeachblacky.model.info.InformationTable;
//import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;
//import ru.nsu.fit.lab4snakepeachblacky.view.InfoPainter;
//
//import java.io.IOException;
//
//
//public class InfoTask implements Runnable {
//    private final NetHandler netHandler;
//    private InformationTable infoTable;
//    private final AnchorPane root;
//    private final InfoPainter painter;
//
//
//    public InfoTask(AnchorPane root) {
//        this.root = root;
//        try {
//            this.netHandler = new NetHandler();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.painter = new InfoPainter();
//        this.infoTable = new InformationTable();
//    }
//
//    @Override
//    public void run() {
//
//        new Thread(this::receiveAnnounceRoutine).start();
//
//        while (true) {
//            netHandler.sendAnnounceMsg();
//            float time = System.currentTimeMillis();
//
//            time = System.currentTimeMillis() - time;
//
//            // Adjust the timing correctly
//            if (time < 1000) {
//                try {
//                    Thread.sleep((long) (1000 - time));
//                } catch (InterruptedException ignore) {
//                }
//            }
//        }
//
//    }
//
//    private void receiveAnnounceRoutine() {
//        var msg = netHandler.receiveAnnounce();
//        parseAnnounceMsg(msg);
//    }
//
//    private void parseAnnounceMsg(SnakesProto.GameMessage msg) {
//        if (!msg.hasAnnouncement()) {
//            throw new IllegalArgumentException("Not announcement msg was passed");
//        }
//        infoTable.getAvailableGames().addGameToList(msg.getAnnouncement().getConfig(),
//                msg.getAnnouncement().getPlayers());
//    }
//
//    public InformationTable getInfoTable() {
//        return infoTable;
//    }
//
//    public InfoPainter getPainter() {
//        return painter;
//    }
//}
