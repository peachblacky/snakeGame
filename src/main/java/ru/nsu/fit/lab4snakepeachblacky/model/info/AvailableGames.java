package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.model.GameConfig;

import java.util.ArrayList;
import java.util.List;

public class AvailableGames {
    private List<GameConfig> games;

    public AvailableGames() {
        this.games = new ArrayList<>();
    }

    public void addGameToList(GameConfig newGame) {
        if(!games.contains(newGame)) {
            games.add(newGame);
        }
    }

    public void removeGameFromList(GameConfig gameToRemove) {
        games.remove(gameToRemove);
    }

    public List<GameConfig> getGames() {
        return games;
    }
}
