package ru.nsu.fit.lab4snakepeachblacky.model.info;

import java.util.ArrayList;
import java.util.List;

public class AvailableGames {
    private List<GameInfo> games;

    public AvailableGames() {
        this.games = new ArrayList<>();
    }

    public void addGameToList(GameInfo newGame) {
        if(!games.contains(newGame)) {
            games.add(newGame);
        }
    }

    public void removeGameFromList(GameInfo gameToRemove) {
        games.remove(gameToRemove);
    }

    public List<GameInfo> getGames() {
        return games;
    }
}
