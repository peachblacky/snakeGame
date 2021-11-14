package ru.nsu.fit.lab4snakepeachblacky.controller;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.fit.lab4snakepeachblacky.model.GameConfig;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Grid;
import ru.nsu.fit.lab4snakepeachblacky.model.info.InformationTable;

public class NetTask implements Runnable{
    private InformationTable infoTable;
    private final GraphicsContext context;


    public NetTask(GraphicsContext context, GameConfig config) {
        this.context = context;
        this.infoTable = new InformationTable();
    }

    @Override
    public void run() {

    }

    public InformationTable getInfoTable() {
        return infoTable;
    }

}
