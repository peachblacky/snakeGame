package ru.nsu.fit.lab4snakepeachblacky.view;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;


public class InfoPainter {

    public static void paintRating(SnakesProto.GameState state, ListView<String> rating) {
        Platform.runLater(
                () -> {
                    rating.getItems().clear();
                    state.getPlayers().getPlayersList().forEach(pl ->
                            rating.getItems().add(pl.getId() + ". " + pl.getName() + " " + pl.getScore()));
                }
        );
    }

    public static void paintGameInfo(SnakesProto.GameState state, ListView<String> gameInfo) {
        Platform.runLater(
                () -> {
                    var master = state.getPlayers().getPlayersList().stream()
                            .filter(pl -> pl.getRole().equals(SnakesProto.NodeRole.MASTER))
                            .findAny()
                            .orElse(null);
                    if(master == null) {
                        return;
                    }
                    gameInfo.getItems().clear();
                    gameInfo.getItems().add("Master: " + master.getName());
                    gameInfo.getItems().add("Size: " + state.getConfig().getWidth()
                            + "x"
                            + state.getConfig().getHeight());
                    gameInfo.getItems().add("Food: " + state.getConfig().getFoodStatic()
                            + "+"
                            + state.getConfig().getFoodPerPlayer() + "x");
                }
        );
    }

    public static void paintAvGameTable(SnakesProto.GameMessage msg, TableView<SnakesProto.GameMessage.AnnouncementMsg> avGametable) {
        if (!msg.hasAnnouncement()) {
            throw new IllegalArgumentException("msg is not announce");
        }
        var newAnnouncement = msg.getAnnouncement();
        Platform.runLater(
                () -> {
                    avGametable.getItems().removeIf(
                            ann -> ann.getConfig().equals(newAnnouncement.getConfig())
                    );
                    avGametable.getItems().add(newAnnouncement);
                }
        );
    }
}
