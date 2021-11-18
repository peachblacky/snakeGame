package ru.nsu.fit.lab4snakepeachblacky.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;
import ru.nsu.fit.lab4snakepeachblacky.view.GridPainter;
import ru.nsu.fit.lab4snakepeachblacky.view.InfoPainter;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTask implements Runnable {
    Thread masterThread;
    Thread normalOrDeputyThread;
    Thread announceThread;
    Thread pingThread;

    AtomicBoolean alive;
    private NetHandler uniSender;
    //    private Lock uniSendLock;
    private NetHandler uniReceiver;
    private AnnounceHandler annSender;
    private AnnounceHandler annReceiver;


    //    private InformationTable infoTable;
    private SnakesProto.NodeRole role;
    private SnakesProto.GameState currentState;
    private Integer stateOrder;

    //    private Grid grid;
    private final GraphicsContext context;
    private ListView<String> rating;
    private ListView<String> gameInfo;
    private TableView<SnakesProto.GameConfig> avGameTable;


    private int frameRate;
    private final float interval;
    private boolean running;
    private boolean paused;
    private boolean keyIsPressed;
    private AtomicInteger msgSeq;

    public GameTask(final GraphicsContext context) {
        try {
            this.uniSender = new NetHandler();
            this.uniReceiver = uniSender;
            this.annSender = new AnnounceHandler();
            this.annReceiver = annSender;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.context = context;
        interval = Constants.STATE_DELAY_MS;
        running = true;
        paused = false;
        keyIsPressed = false;
        role = SnakesProto.NodeRole.NORMAL;
        alive = new AtomicBoolean();
        msgSeq = new AtomicInteger(0);
//        alive.set(false);
    }

    @Override
    public void run() {
        alive.set(true);
        if (role.equals(SnakesProto.NodeRole.MASTER)) {
            masterThread = new Thread(this::stateSendingRoutine);
            masterThread.start();
        } else {
            normalOrDeputyThread = new Thread(this::normalOrDeputyNetRoutine);
            pingThread = new Thread(this::pingRoutine);
            normalOrDeputyThread.start();
            pingThread.start();
        }
        announceThread = new Thread(this::announceRoutine);
        announceThread.start();

        while (alive.get()) {
            // Time the update and paint calls
            float time = System.currentTimeMillis();

            keyIsPressed = false;
//            grid.update();
            GridPainter.paint(currentState, context);
            InfoPainter.paintRating(currentState, rating);

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

    private void stateSendingRoutine() {
        while ((alive.get())) {
            SnakesProto.GameMessage newStateMsg = SnakesProto.GameMessage.newBuilder()
                    .setMsgSeq(msgSeq.getAndIncrement())
                    .setState(
                            SnakesProto.GameMessage.StateMsg.newBuilder()
                                    .setState(currentState)
                                    .build()
                    )
                    .build();
            currentState.getPlayers().getPlayersList().forEach(pl -> uniSender.sendUnicastMsg(newStateMsg, pl.getIpAddress()));
            try {
                Thread.sleep(currentState.getConfig().getStateDelayMs());
            } catch (InterruptedException ignore) {
                break;
            }
        }
    }

    private void announceRoutine() {
        Thread annLisRt = new Thread(this::announceListeningRoutine);
        annLisRt.start();
        if (role.equals(SnakesProto.NodeRole.MASTER)) {
            while (alive.get()) {
                annSender.sendAnnounceMsg(currentState.getPlayers(), currentState.getConfig());
                try {
                    Thread.sleep(Constants.ANN_DELAY_MS);
                } catch (InterruptedException ignore) {
                    annLisRt.interrupt();
                    break;
                }
            }
        }
    }

    private void announceListeningRoutine() {
        while (alive.get()) {
            try {
                SnakesProto.GameMessage msg = annReceiver.receiveAnnounce();
                InfoPainter.paintAvGameTable(msg, avGameTable);

                Thread.sleep(Constants.ANN_DELAY_MS);
            } catch (InterruptedException ignore) {
                break;
            }
        }
    }

    private void pingRoutine() {
        while (alive.get()) {
            currentState.getPlayers().getPlayersList().forEach(pl -> {
//                uniSendLock.lock();
                uniSender.sendUnicastMsg(SnakesProto
                                .GameMessage
                                .newBuilder()
                                .setMsgSeq(msgSeq.incrementAndGet())
                                .setPing(SnakesProto.GameMessage.PingMsg.getDefaultInstance())
                                .build(),
                        pl.getIpAddress()
                );
//                uniSendLock.unlock();
            });
            try {
                Thread.sleep(Constants.PING_DELAY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void steerSnake(SnakesProto.Direction direction) {
        if (role == SnakesProto.NodeRole.MASTER) {
            var mySnake = currentState.getSnakesList().stream()
                    .filter(snake -> snake.getPlayerId() == 1)
                    .findAny()
                    .orElse(null);
            mySnake.toBuilder().setHeadDirection(direction);
        } else {
            var master = currentState.getPlayers().getPlayersList().stream()
                    .filter(pl -> pl.getRole() == SnakesProto.NodeRole.MASTER)
                    .findAny()
                    .orElse(null);
            if (master == null) {
                throw new NullPointerException("Master is null");
            }
            uniSender.sendUnicastMsg(SnakesProto.GameMessage.newBuilder().setSteer(
                            SnakesProto.GameMessage.SteerMsg.newBuilder().setDirection(direction).build()).build(),
                    master.getIpAddress());
        }
    }

    public void connectToGame() {
        //TODO send JOIN message to MASTER and wat for reply
    }

    public void startNewGame() {
        stateOrder = 0;
        role = SnakesProto.NodeRole.MASTER;
        var newState = SnakesProto.GameState.newBuilder()
                .setStateOrder(stateOrder);

        SnakesProto.GamePlayer newMaster = SnakesProto.GamePlayer.newBuilder()
                .setName("PeachBlacky")
                .setId(1)
                .setIpAddress("")
                .setPort(Constants.UNI_PORT)
                .setRole(SnakesProto.NodeRole.MASTER)
                .setScore(0)
                .build();
        SnakesProto.GamePlayers newPlayers = SnakesProto.GamePlayers.newBuilder()
                .addPlayers(newMaster)
                .build();
        newState.setPlayers(newPlayers);

        SnakesProto.GameConfig newConfig = SnakesProto.GameConfig.newBuilder()
                .setWidth(Constants.COLUMNS)
                .setHeight(Constants.ROWS)
                .setFoodStatic(Constants.FOOD_STATIC)
                .setFoodPerPlayer(Constants.FOOD_DYNAMIC)
                .setStateDelayMs(Constants.STATE_DELAY_MS)
                .setPingDelayMs(Constants.PING_DELAY)
                .setNodeTimeoutMs(Constants.NODE_TIMEOUT_MS)
                .build();
        newState.setConfig(newConfig);

        placeSnakes(newState);
        placeFood(newState);

        currentState = newState.build();
    }

    private void placeSnakes(SnakesProto.GameState.Builder state) {
        if (!state.hasPlayers()) {
            return;
        }
        state.getPlayers().getPlayersList().forEach(player -> {
            var newSnake = SnakesProto.GameState.Snake.newBuilder()
                    .setPlayerId(player.getId())
                    .setState(SnakesProto.GameState.Snake.SnakeState.ALIVE)
                    .setHeadDirection(SnakesProto.Direction.DOWN);
//            newSnake.setPoints(newSnake.getPointsCount(), getRandomPoint(state));
//            newSnake.getPointsList().add(getRandomPoint(state));
            newSnake.addPoints(getRandomPoint(state));
            //snake stays still until owner sends STEER_MSG
//            state.getSnakesList().add(newSnake.build());
            state.addSnakes(newSnake.build());
        });
    }

    private void placeFood(SnakesProto.GameState.Builder state) {
        if (!state.hasConfig()) {
            return;
        }
        var config = state.getConfig();
        for (int ind = 0; ind < config.getFoodStatic(); ind++) {
//            System.out.println("Placed");
//            state.setFoods(ind, getRandomPoint(state));
//            state.getFoodsList().add(getRandomPoint(state));
            state.addFoods(getRandomPoint(state));
        }
    }

    private SnakesProto.GameState.Coord getRandomPoint(SnakesProto.GameState.Builder state) {
        var rand = new Random();
        int randCounter = 0;
        var config = state.getConfig();
        SnakesProto.GameState.Coord resultCord = null;
        while (randCounter < config.getWidth() * config.getHeight()) {
            var newCord = SnakesProto.GameState.Coord.newBuilder()
                    .setX(rand.nextInt(config.getWidth()))
                    .setY(rand.nextInt(config.getHeight()))
                    .build();
            resultCord = newCord;
            if (!state.getFoodsList().contains(newCord)) {
                if (state.getSnakesList().stream().noneMatch(snake -> snake.getPointsList().contains(newCord))) {
                    break;
                }
            }
            randCounter++;
        }
        return resultCord;
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

    public SnakesProto.GameState getCurrentState() {
        return currentState;
    }

    //    public InformationTable getInfoTable() {
//        return infoTable;
//    }
//
//    public void setInfoTable(InformationTable table) {
//        this.infoTable = table;
//    }


    public void setRating(ListView<String> rating) {
        this.rating = rating;
    }

    public void setGameInfo(ListView<String> gameInfo) {
        this.gameInfo = gameInfo;
    }

    public void setAvGameTable(TableView<SnakesProto.GameConfig> avGameTable) {
        this.avGameTable = avGameTable;
    }
}
