package ru.nsu.fit.lab4snakepeachblacky.model.gameplay;

import lombok.*;

@AllArgsConstructor
@ToString
public class Cell {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Getter
    @NonNull
    private final int x;
    @Getter
    @NonNull
    private final int y;

    Cell(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Cell move(int dx, int dy) {
        return new Cell(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell point = (Cell) o;
        return x == point.getX() && y == point.getY();
    }

}
