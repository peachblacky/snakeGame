package ru.nsu.fit.lab4snakepeachblacky;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nsu.fit.lab4snakepeachblacky.model.*;
import ru.nsu.fit.lab4snakepeachblacky.controller.GameTask;
import ru.nsu.fit.lab4snakepeachblacky.model.info.EnterButtonTableCell;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

public class SnakeApplication extends Application {

    private GameTask gameTask;
    private Thread gameThread;
    private AnchorPane root;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new AnchorPane();
        Canvas canvas = new Canvas(Constants.FULL_WIDTH, Constants.FULL_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        gameTask = new GameTask(context);
        prepareNewGame();

        canvas.setFocusTraversable(true);

        root.getChildren().add(canvas);

        configureKeyEvents();

        addInterface();

        Scene scene = new Scene(root);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void configureKeyEvents() {
        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> gameTask.steerSnake(SnakesProto.Direction.UP);
                case A -> gameTask.steerSnake(SnakesProto.Direction.LEFT);
                case S -> gameTask.steerSnake(SnakesProto.Direction.DOWN);
                case D -> gameTask.steerSnake(SnakesProto.Direction.RIGHT);
            }
        });
    }

    private void addInterface() {
        addRating();
        addGameInfo();
        addAvailableGames();
        addButtons();
    }

    private void addButtons() {
        //Exit button
        Button exitButton = new Button("Exit");
        AnchorPane.setTopAnchor(exitButton, 20.0 + Constants.RATING_HEIGHT);
        AnchorPane.setLeftAnchor(exitButton, Constants.MAX_GRID_WIDTH + 10.0);
        AnchorPane.setRightAnchor(exitButton,
                Constants.MAX_GRID_WIDTH / 2.0 + 5.0);
        exitButton.setOnMouseClicked(e -> {
            gameTask.stop();
            if (gameThread != null) {
                gameThread.interrupt();
            }
            Platform.exit();
            System.exit(0);
        });
        root.getChildren().add(exitButton);

        //New game button
        Button newGameButton = new Button("New Game");
        AnchorPane.setTopAnchor(newGameButton, 20.0 + Constants.RATING_HEIGHT);
        AnchorPane.setLeftAnchor(newGameButton,
                Constants.PLAY_FIELD_WIDTH + Constants.MAX_GRID_WIDTH / 2.0 + 5.0);
        AnchorPane.setRightAnchor(newGameButton, 10.0);
        newGameButton.setOnMouseClicked(e -> {
            prepareNewGame();
            startGame();
        });
        root.getChildren().add(newGameButton);

    }

    private void addRating() {
        ListView<String> ratingList = new ListView<>();
        AnchorPane.setTopAnchor(ratingList, 10.0);
        AnchorPane.setLeftAnchor(ratingList, Constants.MAX_GRID_WIDTH + 10.0);
        ratingList.setMaxSize(Constants.MAX_GRID_WIDTH / 2.0 - 15.0,
                Constants.RATING_HEIGHT);
        root.getChildren().add(ratingList);
        gameTask.setRating(ratingList);
    }

    private void addGameInfo() {
        ListView<String> gameInfoList = new ListView<>();
        AnchorPane.setTopAnchor(gameInfoList, 10.0);
        AnchorPane.setRightAnchor(gameInfoList, 10.0);
        gameInfoList.setMaxSize(Constants.MAX_GRID_WIDTH / 2.0 - 15.0,
                72);
        root.getChildren().add(gameInfoList);
        gameTask.setGameInfo(gameInfoList);
    }

    private void addAvailableGames() {
        TableView<SnakesProto.GameMessage.AnnouncementMsg> avGamesTable = new TableView<>();
        TableColumn<SnakesProto.GameMessage.AnnouncementMsg, String> masterCol = new TableColumn<>("Master");
        TableColumn<SnakesProto.GameMessage.AnnouncementMsg, String> sizeCol = new TableColumn<>("Size");
        TableColumn<SnakesProto.GameMessage.AnnouncementMsg, String> foodCol = new TableColumn<>("Food");
        TableColumn<SnakesProto.GameMessage.AnnouncementMsg, Button> enterCol = new TableColumn<>("Enter");


        masterCol.setCellValueFactory(p -> {
            var master = p.getValue().getPlayers().getPlayersList().stream()
                    .filter(pl -> pl.getRole().equals(SnakesProto.NodeRole.MASTER))
                    .findAny()
                    .orElse(null);
            if (master == null) {
                return new SimpleStringProperty("Error");
            }
            return new SimpleStringProperty(
                    master.getName() + "[" + master.getIpAddress() + "]"
            );
        });
        sizeCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getConfig().getHeight()
                + "x"
                + p.getValue().getConfig().getWidth()));
        foodCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getConfig().getFoodStatic()
                + "+"
                + p.getValue().getConfig().getFoodPerPlayer()));
        enterCol.setCellFactory(EnterButtonTableCell.forTableColumn("Enter", (SnakesProto.GameMessage.AnnouncementMsg p) -> {
            System.out.println("Enter button pressed");
            enterGame(p);
            return null;
        }));
        avGamesTable.getColumns().addAll(masterCol, sizeCol, foodCol, enterCol);
//        avGamesTable.getItems().add();

        avGamesTable.setMaxSize(Constants.MAX_GRID_WIDTH - 15.0,
                Constants.RATING_HEIGHT);
        avGamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        AnchorPane.setTopAnchor(avGamesTable, Constants.FULL_HEIGHT - Constants.AV_GAMES_HEIGHT + 10.0);
        AnchorPane.setBottomAnchor(avGamesTable, 10.0);
        AnchorPane.setLeftAnchor(avGamesTable, Constants.MAX_GRID_WIDTH + 10.0);
        AnchorPane.setRightAnchor(avGamesTable, 10.0);

        root.getChildren().add(avGamesTable);
        gameTask.setAvGameTable(avGamesTable);
    }

    private void enterGame(SnakesProto.GameMessage.AnnouncementMsg msg) {
        System.out.println("Entering game");
        if (gameThread != null) {
            gameTask.stop();
            if (gameThread.isAlive()) {
                try {
                    gameThread.join(0);
                    System.out.println("TERMINATED RUNNING GAME THREAD");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        gameTask.enterGame(msg);
        startGame();
    }

    private void prepareNewGame() {
        System.out.println("Preparing game");
        if (gameThread != null) {
            gameTask.stop();
            if (gameThread.isAlive()) {
                try {
                    gameThread.join(0);
                    System.out.println("TERMINATED RUNNING GAME THREAD");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        gameTask.startNewGame();
    }

    private void startGame() {
        System.out.println("Starting game");
        gameThread = new Thread(gameTask);
        gameThread.start();
    }
}
