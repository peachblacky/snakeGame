package ru.nsu.fit.lab4snakepeachblacky.model.info;

public class InformationTable {
    private final Rating rating;
    private final GameInfo gameInfo;
    private final AvailableGames availableGames;

    public InformationTable(Rating rating, GameInfo gameInfo, AvailableGames availableGames) {
        this.rating = rating;
        this.gameInfo = gameInfo;
        this.availableGames = availableGames;
    }


    public Rating getRating() {
        return rating;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public AvailableGames getAvailableGames() {
        return availableGames;
    }
}
