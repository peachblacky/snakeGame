package ru.nsu.fit.lab4snakepeachblacky.model;

import javafx.scene.paint.Color;

public class Constants {
    public static final int FULL_WIDTH = 900;
    public static final int FULL_HEIGHT = 600;
    public static final int MAX_GRID_WIDTH = FULL_WIDTH / 2;



    //GameConfig
    public static final int COLUMNS = 15;
    public static final int ROWS = 15;
    public static final int FOOD_STATIC = 1;
    public static final int FOOD_DYNAMIC = 1;
    public static final int STATE_DELAY_MS = 5;
    public static final int PING_DELAY = 100;
    public static final int NODE_TIMEOUT_MS = 800;
    public static final double DEAD_FOOD_GEN_PROB = 0.2;

    //playfield configuration
    public static final int CELL_SIZE = Math.min(FULL_WIDTH / COLUMNS / 2, FULL_HEIGHT / ROWS);
    public static final int PLAY_FIELD_WIDTH = COLUMNS * CELL_SIZE;

    //info configuration
    public static final int RATING_HEIGHT = 200;
    public static final int AV_GAMES_HEIGHT = 355;

    //NET constants
    public static final int UNI_PORT = 1488;
    public static final int MULTI_PORT = 9192;
    public static final long ANN_DELAY_MS = 1000;

    //COLORS
    public static final Color[] COLORS = {
        Color.RED, Color.BROWN, Color.ORANGE, Color.BLUE,
        Color.YELLOW, Color.BLUEVIOLET, Color.BURLYWOOD, Color.DARKORCHID
    };
}
