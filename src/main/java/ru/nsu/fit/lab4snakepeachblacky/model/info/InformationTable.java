package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.model.GameConfig;

public class InformationTable {
    private Rating rating;
    private GameConfig gameConfig;
    private AvailableGames availableGames;


    public InformationTable() {
        rating = new Rating();
        availableGames = new AvailableGames();
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public void setAvailableGames(AvailableGames availableGames) {
        this.availableGames = availableGames;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public AvailableGames getAvailableGames() {
        return availableGames;
    }

//    public static String composeRatingString(User user) {
//        return String.format("",
//                user.getSnakeNumber(),
//                user.getSnakeNumber());
//    }
}
