package ru.nsu.fit.lab4snakepeachblacky.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Grid;
import ru.nsu.fit.lab4snakepeachblacky.model.info.InformationTable;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;
import ru.nsu.fit.lab4snakepeachblacky.view.GridPainter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class GameTask implements Runnable {
    Thread masterThread;
    Thread normalOrDeputyThread;
    Thread announceThread;
    Thread pingThread;

    AtomicBoolean alive;
    private NetHandler uniSender;
    private Lock uniSendLock;
    private NetHandler uniReceiver;
    private AnnounceHandler annSender;
    private AnnounceHandler annReceiver;


    private InformationTable infoTable;
    private Grid grid;
    private final GraphicsContext context;
    private final AnchorPane root;
    private int frameRate;
    private final float interval;
    private boolean running;
    private boolean paused;
    private boolean keyIsPressed;
    private Integer msgSeq;
    private SnakesProto.NodeRole role;

    public GameTask(final GraphicsContext context, AnchorPane root) {
        try {
            this.uniSender = new NetHandler();
            this.uniReceiver = new NetHandler();
            this.annSender = new AnnounceHandler();
            this.annReceiver = new AnnounceHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.root = root;
        this.context = context;
        interval = Constants.STATE_DELAY_MS;
        running = true;
        paused = false;
        keyIsPressed = false;
        msgSeq = 5;
        role = SnakesProto.NodeRole.NORMAL;
        alive.set(false);
    }

    @Override
    public void run() {
        alive.set(true);
        if (role.equals(SnakesProto.NodeRole.MASTER)) {
            masterThread = new Thread(this::masterNetRoutine);
            masterThread.start();
        } else {
            normalOrDeputyThread = new Thread(this::normalOrDeputyNetRoutine);
            pingThread = new Thread(this::pingRoutine);
            normalOrDeputyThread.start();
            pingThread.start();
        }
        announceThread = new Thread(this::announceRoutine);
        announceThread.start();

        while (running) {
            // Time the update and paint calls
            float time = System.currentTimeMillis();

            keyIsPressed = false;
//            grid.update();
            GridPainter.paint(grid, context);

//            if (!grid.getSnakes().get(0).isAlive()) {
//                stop();
//                GridPainter.paintResetMessage(context);
//                break;
//            }

            time = System.currentTimeMillis() - time;

            // Adjust the timing correctly
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    private void normalOrDeputyNetRoutine() {

    }

    private void masterNetRoutine() {

    }

    private void announceRoutine() {
        Thread annLisRt = new Thread(this::announceListeningRoutine);
        annLisRt.start();
        while(alive.get()) {
            if (role.equals(SnakesProto.NodeRole.MASTER)) {
                annSender.sendAnnounceMsg();
            }
            try {
                Thread.sleep(Constants.ANN_DELAY_MS);
            } catch (InterruptedException ignore) {
                annLisRt.interrupt();
                break;
            }
        }
    }

    private void announceListeningRoutine() {
        while(alive.get()) {
            try {
                SnakesProto.GameMessage msg = annReceiver.receiveAnnounce();
//                infoTable.


                Thread.sleep(Constants.ANN_DELAY_MS);
            } catch (InterruptedException ignore) {
                break;
            }
        }
    }

    private void pingRoutine() {
        while (alive.get()) {
            infoTable.getCurGamePlayers().getPlayersList().forEach(pl -> {
                uniSendLock.lock();
                uniSender.sendUnicastMsg(SnakesProto
                                .GameMessage
                                .newBuilder()
                                .setPing(SnakesProto.GameMessage.PingMsg.getDefaultInstance())
                                .build(),
                        pl.getIpAddress()
                );
                uniSendLock.unlock();
            });
            try {
                Thread.sleep(Constants.PING_DELAY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void connectToGame() {
        //TODO send JOIN message to MASTER and wat for reply
    }

    public void startNewGame() {
        //TODO start new game
    }

    public void setMode(SnakesProto.NodeRole newRole) {
        role = newRole;
    }

    public void stop() {
        alive.set(false);
    }

    public boolean isKeyPressed() {
        return keyIsPressed;
    }

    public void setKeyPressed() {
        keyIsPressed = true;
    }

    public void resume() {
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public boolean isRunning() {
        return running;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public InformationTable getInfoTable() {
        return infoTable;
    }

    public void setInfoTable(InformationTable table) {
        this.infoTable = table;
    }
}
