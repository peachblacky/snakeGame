package ru.nsu.fit.lab4snakepeachblacky.model.gameplay;

import javafx.scene.paint.Color;
import lombok.Getter;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    public Color color = Color.CORNSILK;
    public static final Color DEAD_CELL = Color.RED;

    private SnakesProto.GamePlayer owner;
    private Grid grid;
    private int length;
    @Getter
    private List<Cell> cells;
    @Getter
    private Cell head;
    private int xVel;
    private int yVel;
    private boolean isAlive;

    public List<Cell> getCells() {
        return cells;
    }

    public Cell getHead() {
        return head;
    }

    public Snake(SnakesProto.GamePlayer owner, Grid grid, Cell initialPoint) {
        this.owner = owner;
//        if(owner.getRole() == ) {
//            color = Color.YELLOW;
//        }
        length = 1;
        cells = new LinkedList<>();
        cells.add(initialPoint);
        head = initialPoint;
        isAlive = true;
        this.grid = grid;
        xVel = 0;
        yVel = 0;
    }

    public void move() {
        if(!isStill()) {
            shift(head.move(xVel, yVel));
        }
    }

    public void extend() {
        if (!isStill()) {
            grow(head.move(xVel, yVel));
        }
    }

    public void setUp() {
        if (yVel == 1 && length > 1) return;
        xVel = 0;
        yVel = -1;
    }

    public void setDown() {
        if (yVel == -1 && length > 1) return;
        xVel = 0;
        yVel = 1;
    }

    public void setLeft() {
        if (xVel == 1 && length > 1) return;
        xVel = -1;
        yVel = 0;
    }

    public void setRight() {
        if (xVel == -1 && length > 1) return;
        xVel = 1;
        yVel = 0;
    }


    private void grow(Cell cell) {
        length += 1;
        addCell(cell);
    }


    private void shift(Cell cell) {
        addCell(cell);
        cells.remove(0);
    }

    public boolean isAlive() {
        return isAlive;
    };

    private void addCell(Cell cell) {
        cell = grid.wrap(cell);
        isAlive &= !cells.contains(cell);
        cells.add(cell);
        head = cell;
    }

    private boolean isStill() {
        return xVel == 0 & yVel == 0;
    }

}
