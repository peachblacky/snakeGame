package ru.nsu.fit.lab4snakepeachblacky.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

/**
 * @author Subhomoy Haldar
 * @version 2016.12.1state7
 */
public class GridPainter {


    public static void paint(SnakesProto.GameState state, GraphicsContext gc) {
        int cellSize = Math.min(Constants.FULL_WIDTH / state.getConfig().getWidth() / 2,
                Constants.FULL_HEIGHT / state.getConfig().getHeight());
        gc.setFill(new Color(38.0 / 256, 81.0 / 256, 39.0 / 256, 1));
        gc.fillRect(0, 0, state.getConfig().getWidth() * cellSize, state.getConfig().getHeight() * cellSize);
        gc.setFill(Color.BLACK);

        paintApples(state, gc);
        paintSnake(state, gc);

        // The score
//        gc.setFill(Color.BEIGE);
//        gc.fillText("Score : " + 100 * state.getSnakes().get(0).getCells().size(), 10, 490);

    }

    private static void paintApples(SnakesProto.GameState state, GraphicsContext gc) {
        gc.setFill(Color.PAPAYAWHIP);
        state.getFoodsList().forEach(food -> paintPoint(food, state.getConfig(), gc));
    }

    private static void paintSnake(SnakesProto.GameState state, GraphicsContext gc) {
        state.getSnakesList().forEach(snake -> {
            gc.setFill(Constants.COLORS[snake.getPlayerId()]);
            snake.getPointsList().forEach(point -> paintPoint(point, state.getConfig(), gc));
        });
    }

    private static void paintPoint(SnakesProto.GameState.Coord cell, SnakesProto.GameConfig config, GraphicsContext gc) {
        int newCellSize = Math.min(Constants.FULL_WIDTH / config.getWidth() / 2,
                Constants.FULL_HEIGHT / config.getHeight());
        gc.fillRect(cell.getX() * newCellSize,
                cell.getY() * newCellSize, newCellSize, newCellSize);
    }

    public static void paintResetMessage(GraphicsContext gc) {
        gc.setFill(Color.AQUAMARINE);
        gc.fillText("Hit ENTER to reset.", 10, 10);
    }
}
