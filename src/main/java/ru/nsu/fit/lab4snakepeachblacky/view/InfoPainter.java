package ru.nsu.fit.lab4snakepeachblacky.view;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.info.InformationTable;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;


public class InfoPainter {

//    public static void paint(SnakesProto.GameState state, ListView<String> rating, ListView<String> gameInfo) {
//        paintRating(state, rating);
//    }

    public static void paintRating(SnakesProto.GameState state, ListView<String> rating) {
        Platform.runLater(
                () -> {
                    rating.getItems().clear();
                    state.getPlayers().getPlayersList().forEach(pl ->
                            rating.getItems().add(pl.getId() + ". " + pl.getName() + " " + pl.getScore()));
                }
        );
//        rating.getItems().clear();
    }

    public static void paintGameInfo(SnakesProto.GameState state, ListView<String> gameInfo) {
        Platform.runLater(
                () -> {
                    gameInfo.getItems().clear();
                    state.getPlayers().getPlayersList().forEach(pl ->
                            gameInfo.getItems().add(pl.getId() + ". " + pl.getName() + " " + pl.getScore()));
                }
        );
    }

    public static void paintAvGameTable(SnakesProto.GameMessage msg, TableView<SnakesProto.GameMessage.AnnouncementMsg> avGametable) {
        if (!msg.hasAnnouncement()) {
            throw new IllegalArgumentException("msg is not announce");
        }
        var newGameConfig = msg.getAnnouncement();
        Platform.runLater(
                () -> {
//                    if(!avGametable.getItems().contains(newGameConfig)) {
                    avGametable.getItems().add(newGameConfig);
//                    }
                }
        );
    }
}
