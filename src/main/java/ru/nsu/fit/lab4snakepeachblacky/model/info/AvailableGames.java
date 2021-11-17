package ru.nsu.fit.lab4snakepeachblacky.model.info;

import javafx.util.Pair;
import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AvailableGames {
    private List<Pair<SnakesProto.GameConfig, SnakesProto.GamePlayers>> games;

    public AvailableGames() {
        this.games = new ArrayList<>();
    }

    public void addGameToList(SnakesProto.GameConfig newGame, SnakesProto.GamePlayers newGamePlayers) {
        var newPair = new Pair<>(newGame, newGamePlayers);
        if (!games.contains(newPair)) {
            games.add(0, newPair);
        }
    }

    public void removeGameFromList(SnakesProto.GameConfig gameToRemove) {
        games.remove(gameToRemove);
    }

    public String getGameMaster(SnakesProto.GameConfig game) {
        var searchRes = games
                .stream().filter(pair -> pair.getKey().equals(game)).collect(Collectors.toList());
        if(searchRes.isEmpty()) {
            return "GameNotFound";
        }
        var players = searchRes.get(0).getValue();
        var master = players.getPlayersList()
                .stream()
                .filter(pl -> pl.getRole().equals(SnakesProto.NodeRole.MASTER)).collect(Collectors.toList()).get(0);
        return master.getName() + "[" + master.getIpAddress() + "]";
    }

    public List<Pair<SnakesProto.GameConfig, SnakesProto.GamePlayers>> getGames() {
        return games;
    }
}
