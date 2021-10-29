package ru.nsu.fit.lab4snakepeachblacky.model.gameplay;

import javafx.scene.paint.Color;
import lombok.NonNull;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
    public static final Color GRID_COLOR = new Color(38.0/256, 81.0/256, 39.0/256, 1);

    @NonNull
    private final int columns;

    @NonNull
    private final int rows;

    @NonNull
    private final List<Snake> snakes;

    @NonNull
    private final Apple apple;

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Apple getApple() {
        return apple;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public Grid(final double width, final double height) {
        rows = (int) width / Constants.CELL_SIZE;
        columns = (int) height / Constants.CELL_SIZE;

        // initialize the snake at the centre of the screen
        snakes = new ArrayList<>();
        snakes.add(new Snake(this, new Cell(rows / 2, columns / 2)));

        // put the food at a random location
        apple = new Apple(getRandomPoint());
    }

    public Cell wrap(Cell point) {
        int x = point.getX();
        int y = point.getY();
        if (x >= rows) x = 0;
        if (y >= columns) y = 0;
        if (x < 0) x = rows - 1;
        if (y < 0) y = columns - 1;
        return new Cell(x,y);
    }

    private Cell getRandomPoint() {
        Random random = new Random();
        Cell point;
        do {
            point = new Cell(random.nextInt(rows), random.nextInt(columns));
        } while (point.equals(snakes.get(0).getHead()));
        return point;
    }

    public void update() {
        if (apple.getLocation().equals(snakes.get(0).getHead())) {
            snakes.get(0).extend();
            apple.setLocation(getRandomPoint());
        } else {
            snakes.get(0).move();
        }
    }

    public double getWidth() {
        return rows * Constants.CELL_SIZE;
    }

    public double getHeight() {
        return columns * Constants.CELL_SIZE;
    }
}