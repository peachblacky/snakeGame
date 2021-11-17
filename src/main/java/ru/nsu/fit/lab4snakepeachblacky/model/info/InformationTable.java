package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

public class InformationTable {
    private Rating rating;
    private SnakesProto.GameConfig curGameConfig;
    private SnakesProto.GamePlayers curGamePlayers;
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

    public void setCurGameConfig(SnakesProto.GameConfig curGameConfig) {
        this.curGameConfig = curGameConfig;
    }

    public SnakesProto.GameConfig getCurGameConfig() {
        return curGameConfig;
    }

    public AvailableGames getAvailableGames() {
        return availableGames;
    }

    public SnakesProto.GamePlayers getCurGamePlayers() {
        return curGamePlayers;
    }
//    public static String composeRatingString(User user) {
//        return String.format("",
//                user.getSnakeNumber(),
//                user.getSnakeNumber());
//    }
}
