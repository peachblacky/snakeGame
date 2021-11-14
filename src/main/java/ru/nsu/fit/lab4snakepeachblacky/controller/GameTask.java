package ru.nsu.fit.lab4snakepeachblacky.controller;

import javafx.scene.canvas.GraphicsContext;
import ru.nsu.fit.lab4snakepeachblacky.model.Constants;
import ru.nsu.fit.lab4snakepeachblacky.model.gameplay.Grid;
import ru.nsu.fit.lab4snakepeachblacky.view.GridPainter;

public class GameTask implements Runnable {
    private final Grid grid;
    private final GraphicsContext context;
    private int frameRate;
    private final float interval;
    private boolean running;
    private boolean paused;
    private boolean keyIsPressed;

    public GameTask(final Grid grid, final GraphicsContext context) {
        this.grid = grid;
        this.context = context;
        interval = Constants.STATE_DELAY_MS;
        running = true;
        paused = false;
        keyIsPressed = false;
    }

    @Override
    public void run() {
        while (running) {
            // Time the update and paint calls
            float time = System.currentTimeMillis();

            keyIsPressed = false;
            grid.update();
            GridPainter.paint(grid, context);

            if (!grid.getSnakes().get(0).isAlive()) {
                stop();
                GridPainter.paintResetMessage(context);
                break;
            }

            time = System.currentTimeMillis() - time;

            // Adjust the timing correctly
            if (time < interval) {
                try {
                    Thread.sleep((long) (interval - time));
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    public boolean isKeyPressed() {
        return keyIsPressed;
    }

    public void setKeyPressed() {
        keyIsPressed = true;
    }

    public void resume() {
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public boolean isRunning() {
        return running;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}
