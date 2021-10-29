package ru.nsu.fit.lab4snakepeachblacky.model.gameplay;

import javafx.scene.paint.Color;
import lombok.NonNull;

public class Apple {
    public static final Color COLOR = Color.CRIMSON;

    public Apple(@NonNull Cell location) {
        this.location = location;
    }

    public void setLocation(Cell location) {
        this.location = location;
    }

    public Cell getLocation() {
        return location;
    }

    @NonNull
    private Cell location;

}
