package ru.nsu.fit.lab4snakepeachblacky.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;
import ru.nsu.fit.lab4snakepeachblacky.view.GridPainter;
import ru.nsu.fit.lab4snakepeachblacky.view.InfoPainter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameTask implements Runnable {
    Thread stateSendingThread;
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
    private Integer id;
    private SnakesProto.GameState currentState;
    Lock stateLock = new ReentrantLock();
    Condition stateCond = stateLock.newCondition();
    private Integer stateOrder;

    //    private Grid grid;
    private final GraphicsContext context;
    private ListView<String> rating;
    private ListView<String> gameInfo;
    Boolean isPaintTurn = true;
    private TableView<SnakesProto.GameMessage.AnnouncementMsg> avGameTable;


    private boolean hasDeputy;
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
        role = SnakesProto.NodeRole.NORMAL;
        alive = new AtomicBoolean();
        msgSeq = new AtomicInteger(0);
        hasDeputy = false;
//        alive.set(false);
    }

    @Override
    public void run() {
        alive.set(true);
        if (role.equals(SnakesProto.NodeRole.MASTER)) {
            stateSendingThread = new Thread(this::stateSendingRoutine);
            stateSendingThread.start();
            masterThread = new Thread(this::masterRoutine);
            masterThread.start();
        } else {
            normalOrDeputyThread = new Thread(this::normalOrDeputyNetRoutine);
            normalOrDeputyThread.start();
        }
        pingThread = new Thread(this::pingRoutine);
        pingThread.start();
        announceThread = new Thread(this::announceRoutine);
        announceThread.start();

        while (alive.get()) {

//            grid.update();
            stateLock.lock();
            try {
                while (!isPaintTurn) {
                    stateCond.await();
                }
//                System.out.println("PAINT");
                GridPainter.paint(currentState, context);
                InfoPainter.paintRating(currentState, rating);
                isPaintTurn = false;
                stateCond.signalAll();
            } catch (InterruptedException e) {
                break;
            } finally {
                stateLock.unlock();
            }

            try {
                Thread.sleep(currentState.getConfig().getStateDelayMs());
            } catch (InterruptedException ignore) {
                System.out.println("Interrupted main game thread");
            }
        }
    }

    private void normalOrDeputyNetRoutine() {

    }

    private void masterRoutine() {
//        Long time = 0L;
        var time = new Object(){ long time = 0;};
        Map<String, Long> playerTimeouts = new HashMap<>();
        while (alive.get()) {
            time.time = System.currentTimeMillis();
            var msgWrap = uniReceiver.receiveUnicastMsg();
            var msg = msgWrap.getMsg();
            if(msg.hasJoin()) {
                var stateB = SnakesProto.GameState.newBuilder(currentState);
                SnakesProto.GamePlayer newPlayer = SnakesProto.GamePlayer.newBuilder()
                        .setName(msg.getJoin().getName())
                        .setId(stateB.getPlayers().getPlayersCount() + 1)
                        .setIpAddress(msgWrap.getIp())
                        .setPort(Constants.UNI_PORT)
                        .setRole(SnakesProto.NodeRole.NORMAL)
                        .setScore(0)
                        .build();
                SnakesProto.GamePlayers newPlayers = SnakesProto.GamePlayers.newBuilder(stateB.getPlayers())
                        .addPlayers(newPlayer)
                        .build();
                stateB.setPlayers(newPlayers);
                stateLock.lock();
                currentState = stateB.build();
                //ACK
                uniSender.sendUnicastMsg(SnakesProto.GameMessage.newBuilder()
                                .setMsgSeq(msgSeq.getAndIncrement())
                                .setAck(SnakesProto.GameMessage.AckMsg.getDefaultInstance())
                                .setReceiverId(currentState.getPlayers().getPlayersCount() + 1)
                        .build(), msgWrap.getIp());
                if(!hasDeputy) {
                    chooseDeputy();
                }
                stateLock.unlock();
                playerTimeouts.put(msgWrap.getIp(), 0L);
            }
            if(msg.hasPing()) {
                if(playerTimeouts.containsKey(msgWrap.getIp())) {
                    stateLock.lock();
                    playerTimeouts.put(msgWrap.getIp(), 0L);
                    stateLock.unlock();
                }
            }
            if(msg.hasSteer()) {
                if(playerTimeouts.containsKey(msgWrap.getIp())) {
                    stateLock.lock();
                    var mySnake = currentState.getSnakesList().stream()
                            .filter(snake -> snake.getPlayerId() == msg.getSenderId())
                            .findAny()
                            .orElse(null);
                    if (mySnake == null) {
                        stateLock.unlock();
                        return;
                    }
                    if (switch (mySnake.getHeadDirection()) {
                        case UP -> msg.getSteer().getDirection() != SnakesProto.Direction.DOWN;
                        case DOWN -> msg.getSteer().getDirection() != SnakesProto.Direction.UP;
                        case LEFT -> msg.getSteer().getDirection() != SnakesProto.Direction.RIGHT;
                        case RIGHT -> msg.getSteer().getDirection() != SnakesProto.Direction.LEFT;

                    }) {
                        var newState = currentState.toBuilder()
                                .removeSnakes(currentState.getSnakesList().indexOf(mySnake));
                        mySnake = mySnake.toBuilder().setHeadDirection(msg.getSteer().getDirection()).build();
                        newState.addSnakes(mySnake);
                        currentState = newState.build();
                    }
                    stateLock.unlock();
                }
            }
            if(msg.hasAck()) {
                //TODO if i realise that they will be sent to master at all
            }
            time.time = System.currentTimeMillis() - time.time;

            playerTimeouts.entrySet().removeIf(entry -> {
                entry.setValue(entry.getValue() + time.time);
                stateLock.lock();
                if(entry.getValue() > currentState.getConfig().getNodeTimeoutMs()) {
                    deletePlayerByIp(entry.getKey());
                    stateLock.unlock();
                    return true;
                }
                stateLock.unlock();
                return false;
            });
        }
    }

    //only master
    private void deletePlayerByIp(String ip) {
        var player = currentState.getPlayers().getPlayersList().stream()
                .filter(pl -> pl.getIpAddress().equals(ip))
                .findAny()
                .orElse(null);
        if(player == null) {
            return;
        }
        var stateB = SnakesProto.GameState.newBuilder(currentState);
        SnakesProto.GamePlayers newPlayers = SnakesProto.GamePlayers.newBuilder(stateB.getPlayers())
                .removePlayers(stateB.getPlayers().getPlayersList().indexOf(player))
                .build();
        stateB.setPlayers(newPlayers);
        currentState = stateB.build();
        if(player.getRole() == SnakesProto.NodeRole.DEPUTY) {
            chooseDeputy();
        }
    }

    //only master
    private void chooseDeputy() {
        var players = currentState.getPlayers();
        var newDeputy = players.getPlayersList().stream()
                .filter(pl -> pl.getRole() == SnakesProto.NodeRole.NORMAL)
                .findAny()
                .orElse(null);
        if(newDeputy == null) {
            alive.set(false);
            return;
        }
        var stateB = SnakesProto.GameState.newBuilder(currentState);
        stateB.setPlayers(
                players.toBuilder()
                        .removePlayers(players.getPlayersList().indexOf(newDeputy))
                        .addPlayers(newDeputy.toBuilder().setRole(SnakesProto.NodeRole.DEPUTY).build())
                        .build()
        );
        currentState = stateB.build();
    }

    private void stateSendingRoutine() {
        while ((alive.get())) {
            stateLock.lock();
            try {
                while (isPaintTurn) {
                    stateCond.await();
                }
//                System.out.println("STATE");
                updateState();
                isPaintTurn = true;
                stateCond.signalAll();
            } catch (InterruptedException e) {
                break;
            } finally {
                stateLock.unlock();
            }
//            System.out.println("State updated");
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
                msg = setMasterIdAnnounce(msg.getAnnouncement());
                InfoPainter.paintAvGameTable(msg, avGameTable);
                Thread.sleep(Constants.ANN_DELAY_MS);
            } catch (InterruptedException ignore) {
                break;
            }
        }
    }

    private SnakesProto.GameMessage setMasterIdAnnounce(SnakesProto.GameMessage.AnnouncementMsg msg) {
        var master = msg.getPlayers().getPlayersList().stream()
                .filter(pl -> pl.getRole().equals(SnakesProto.NodeRole.MASTER))
                .findAny()
                .orElse(null);
        if(master == null) {
            throw new NullPointerException("Master of entered game is null");
        }
        //TODO
    }

    private void pingRoutine() {
        while (alive.get()) {
            currentState.getPlayers().getPlayersList().forEach(pl -> {
//                uniSendLock.lock();
                uniSender.sendUnicastMsg(SnakesProto.GameMessage.newBuilder()
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
        stateLock.lock();
        if (role == SnakesProto.NodeRole.MASTER) {
            var mySnake = currentState.getSnakesList().stream()
                    .filter(snake -> snake.getPlayerId() == 1)
                    .findAny()
                    .orElse(null);
            if (mySnake == null) {
                stateLock.unlock();
                return;
            }
            if (switch (mySnake.getHeadDirection()) {
                case UP -> direction != SnakesProto.Direction.DOWN;
                case DOWN -> direction != SnakesProto.Direction.UP;
                case LEFT -> direction != SnakesProto.Direction.RIGHT;
                case RIGHT -> direction != SnakesProto.Direction.LEFT;

            }) {
                var newState = currentState.toBuilder()
                        .removeSnakes(currentState.getSnakesList().indexOf(mySnake));
                mySnake = mySnake.toBuilder().setHeadDirection(direction).build();
                newState.addSnakes(mySnake);
                currentState = newState.build();
            }
        } else {
            if (role == SnakesProto.NodeRole.VIEWER) {
                stateLock.unlock();
                return;
            }
            var master = currentState.getPlayers().getPlayersList().stream()
                    .filter(pl -> pl.getRole() == SnakesProto.NodeRole.MASTER)
                    .findAny()
                    .orElse(null);
            if (master == null) {
                throw new NullPointerException("Master is null");
            }
            uniSender.sendUnicastMsg(SnakesProto.GameMessage.newBuilder()
                            .setSteer(SnakesProto.GameMessage.SteerMsg.newBuilder().setDirection(direction).build())
                            .setSenderId(id)
                            .build(),
                    master.getIpAddress());
        }
        stateLock.unlock();
    }

    //only for master
    private void updateState() {
        currentState.getSnakesList().forEach(snake -> {
            var moveCoord = SnakesProto.GameState.Coord.newBuilder();
            var headCoord = snake.getPointsList().get(0);
            var newSnake = snake.toBuilder();
            switch (snake.getHeadDirection()) {
                case UP -> {
                    newSnake.addPoints(0, normalizePoint(moveCoord.
                            setX(headCoord.getX())
                            .setY(headCoord.getY() - 1)
                            .build())
                    );
                }
                case DOWN -> {
                    System.out.println("DOWN");
                    newSnake.addPoints(0, normalizePoint(moveCoord.
                            setX(headCoord.getX())
                            .setY(headCoord.getY() + 1)
                            .build())
                    );
                }
                case LEFT -> {
                    newSnake.addPoints(0, normalizePoint(moveCoord.
                            setX(headCoord.getX() - 1)
                            .setY(headCoord.getY())
                            .build())
                    );
                }
                case RIGHT -> {
                    newSnake.addPoints(0, normalizePoint(moveCoord.
                            setX(headCoord.getX() + 1)
                            .setY(headCoord.getY())
                            .build())
                    );
                }
            }
            //checking collisions
            headCoord = newSnake.getPointsList().get(0);
            System.out.println("Snake head " + headCoord.getX() + " " + headCoord.getY());
            var stateB = currentState.toBuilder();
            if (checkFoodCollide(headCoord)) {
                plusScore(snake.getPlayerId());
                var eatenFood = currentState.getFoodsList().indexOf(headCoord);
                stateB.removeFoods(eatenFood);
                placeFood(stateB);
            } else if (checkSnakesCollide(headCoord)) {
                makePlayerViewer(snake.getPlayerId());
                stateB.removeSnakes(stateB.getSnakesList().indexOf(snake));
                snake.getPointsList().forEach(point -> {
                    if (Math.random() < Constants.DEAD_FOOD_GEN_PROB) {
                        stateB.addFoods(point);
                    }
                });
                currentState = stateB.build();
                return;
            } else {
                newSnake.removePoints(newSnake.getPointsCount() - 1);
            }
            stateB.removeSnakes(stateB.getSnakesList().indexOf(snake));
            stateB.addSnakes(newSnake);
            currentState = stateB.build();
        });
    }

    //only master
    private void makePlayerViewer(int id) {
        var stateB = SnakesProto.GameState.newBuilder(currentState);
        var foundPlayer = currentState.getPlayers().getPlayersList().stream()
                .filter(pl -> pl.getId() == id)
                .findAny()
                .orElse(null);
        if (foundPlayer == null) {
            return;
        }
        if (foundPlayer.getRole() == SnakesProto.NodeRole.MASTER) {
            return;
        }
        stateB.setPlayers(SnakesProto.GamePlayers.newBuilder(stateB.getPlayers())
                .removePlayers(stateB.getPlayers().getPlayersList().indexOf(foundPlayer))
                .addPlayers(foundPlayer.toBuilder().setRole(SnakesProto.NodeRole.VIEWER).build())
                .build());
        currentState = stateB.build();
        uniSender.sendUnicastMsg(SnakesProto.GameMessage.newBuilder()
                .setMsgSeq(msgSeq.getAndIncrement())
                .setRoleChange(
                        SnakesProto.GameMessage.RoleChangeMsg.newBuilder()
                                .setSenderRole(SnakesProto.NodeRole.MASTER)
                                .setReceiverRole(SnakesProto.NodeRole.VIEWER)
                                .build()
                )
                .build(), foundPlayer.getIpAddress());
    }

    //only master
    private SnakesProto.GameState.Coord normalizePoint(SnakesProto.GameState.Coord point) {
        var normalizedPoint = SnakesProto.GameState.Coord.newBuilder(point);
        if (point.getX() == currentState.getConfig().getWidth()) {
            normalizedPoint.setX(0);
        } else if (point.getX() < 0) {
            normalizedPoint.setX(currentState.getConfig().getWidth() - 1);
        }
        if (point.getY() == currentState.getConfig().getHeight()) {
            normalizedPoint.setY(0);
        } else if (point.getY() < 0) {
            normalizedPoint.setY(currentState.getConfig().getHeight() - 1);
        }
        return normalizedPoint.build();
    }

    //only master
    private void plusScore(int playerId) {
//        var stateB = SnakesProto.GameState.newBuilder(currentState);
        currentState.getPlayers().getPlayersList().forEach(pl -> {
            if (pl.getId() == playerId) {
                pl = pl.toBuilder().setScore(pl.getScore() + 1).build();
            }
        });
//        currentState = stateB.build();
        //TODO
    }

    //only master
    private boolean checkFoodCollide(SnakesProto.GameState.Coord headCoord) {
        return currentState.getFoodsList().contains(headCoord);
    }

    //only master
    private boolean checkSnakesCollide(SnakesProto.GameState.Coord headCoord) {
        return currentState.getSnakesList().stream()
                .anyMatch(snake -> snake.getPointsList().contains(headCoord));
    }

    public void enterGame(SnakesProto.GameMessage.AnnouncementMsg msg) {
        var master = msg.getPlayers().getPlayersList().stream()
                .filter(pl -> pl.getRole().equals(SnakesProto.NodeRole.MASTER))
                .findAny()
                .orElse(null);
        if(master == null) {
            throw new NullPointerException("Master of entered game is null");
        }
        uniSender.sendUnicastMsg(
                SnakesProto.GameMessage.newBuilder()
                        .setJoin(
                                SnakesProto.GameMessage.JoinMsg.newBuilder()
                                        .setName("Rostislavus")
                                        .build()
                        )
                        .build(),
                master.getIpAddress()
        );
        //TODO send JOIN message to MASTER and wait for reply
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
        for (int i = 0; i < newConfig.getFoodStatic() + 1; i++) {
            placeFood(newState);
        }

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
            newSnake.addPoints(getRandomPoint(state));
            state.addSnakes(newSnake.build());
        });
    }

    private void placeFood(SnakesProto.GameState.Builder state) {
        state.addFoods(getRandomPoint(state));
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


    public void stop() {
        alive.set(false);
    }

    public SnakesProto.GameState getCurrentState() {
        return currentState;
    }

    public void setRating(ListView<String> rating) {
        this.rating = rating;
    }

    public void setGameInfo(ListView<String> gameInfo) {
        this.gameInfo = gameInfo;
    }

    public void setAvGameTable(TableView<SnakesProto.GameMessage.AnnouncementMsg> avGameTable) {
        this.avGameTable = avGameTable;
    }
}
