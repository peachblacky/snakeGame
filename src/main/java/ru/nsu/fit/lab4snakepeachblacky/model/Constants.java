package ru.nsu.fit.lab4snakepeachblacky.model;

public class Constants {
    public static final int FULL_WIDTH = 900;
    public static final int FULL_HEIGHT = 600;



    //TODO add GameConfig constants for new game starting
    //GameConfig
    public static final int COLUMNS = 10;
    public static final int ROWS = 10;
    public static final int FOOD_STATIC = 1;
    public static final int FOOD_DYNAMIC = 1;
    public static final int STATE_DELAY_MS = 100;
    public static final int PING_DELAY = 100;
    public static final int NODE_TIMEOUT_MS = 800;

    //playfield configuration
    public static final int CELL_SIZE = Math.min(FULL_WIDTH / COLUMNS / 2, FULL_HEIGHT / ROWS);
    public static final int PLAY_FIELD_WIDTH = COLUMNS * CELL_SIZE;
    public static final int PLAY_FIELD_HEIGHT = ROWS * CELL_SIZE;

    //info configuration
    public static final int RATING_WIDTH = 100;
    public static final int RATING_HEIGHT = 200;
    public static final int AV_GAMES_WIDTH = FULL_WIDTH / 2 - 20;
    public static final int AV_GAMES_HEIGHT = 355;
}
