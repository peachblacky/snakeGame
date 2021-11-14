package ru.nsu.fit.lab4snakepeachblacky.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Apple;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Cell;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Grid;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Snake;

/**
 * @author Subhomoy Haldar
 * @version 2016.12.17
 */
public class GridPainter {


    public static void paint(Grid grid, GraphicsContext gc) {
        gc.setFill(Grid.GRID_COLOR);
        gc.fillRect(0, 0, grid.getWidth(), grid.getHeight());
        gc.setFill(Color.BLACK);

        paintApples(grid, gc);
        paintSnake(grid, gc);

        // The score
        gc.setFill(Color.BEIGE);
        gc.fillText("Score : " + 100 * grid.getSnakes().get(0).getCells().size(), 10, 490);
    }

    private static void paintApples(Grid grid, GraphicsContext gc) {
        gc.setFill(Apple.COLOR);
        grid.getApples().forEach(apple -> {
           paintPoint(apple.getLocation(), gc);
        });
//        paintPoint(grid.getApple().getLocation(), gc);
    }

    private static void paintSnake(Grid grid, GraphicsContext gc) {
//        Snake snake = grid.getSnakes().get(0);
        grid.getSnakes().forEach(snake -> {
            gc.setFill(Snake.COLOR);
            snake.getCells().forEach(point -> paintPoint(point, gc));
            if (!snake.isAlive()) {
                gc.setFill(Snake.DEAD_CELL);
                paintPoint(snake.getHead(), gc);
            }
        });
    }

    private static void paintPoint(Cell cell, GraphicsContext gc) {
        gc.fillRect(cell.getX() * Constants.CELL_SIZE,
                cell.getY() * Constants.CELL_SIZE, Constants.CELL_SIZE, Constants.CELL_SIZE);
    }

    public static void paintResetMessage(GraphicsContext gc) {
        gc.setFill(Color.AQUAMARINE);
        gc.fillText("Hit ENTER to reset.", 10, 10);
    }
}
