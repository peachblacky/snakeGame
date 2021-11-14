package ru.nsu.fit.lab4snakepeachblacky.model.info;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class AvailableGameWrap {
    public final StringProperty master;
    public final StringProperty gridSize;
    public final StringProperty food;

    public AvailableGameWrap(String master, String gridSize, String food) {
        this.master = new SimpleStringProperty(master);
        this.gridSize = new SimpleStringProperty(gridSize);
        this.food = new SimpleStringProperty(food);
    }

}
