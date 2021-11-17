package ru.nsu.fit.lab4snakepeachblacky.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import ru.nsu.fit.lab4snakepeachblacky.model.info.InformationTable;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;


public class InfoPainter {
    private TableView<SnakesProto.GameConfig> avGameTable;
    private ListView<String> curGameInfo;
    private ListView<String> rating;


    public static void paint(InformationTable infoTable, GraphicsContext gc) {

    }

    public void setAvGameTable(TableView<SnakesProto.GameConfig> avGameTable) {
        this.avGameTable = avGameTable;
    }

    public void setCurGameInfo(ListView<String> curGameInfo) {
        this.curGameInfo = curGameInfo;
    }

    public void setRating(ListView<String> rating) {
        this.rating = rating;
    }
}
