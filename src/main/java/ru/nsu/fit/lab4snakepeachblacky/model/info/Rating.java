package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.proto.SnakesProto;

import java.util.ArrayList;
import java.util.List;

public class Rating {
    private List<SnakesProto.GamePlayer> rating;

    public Rating() {
        this.rating = new ArrayList<>();
    }

    public void addUserToChart(SnakesProto.GamePlayer newUser) {
        if(!rating.contains(newUser)) {
            rating.add(newUser);
        }
    }

    public void removeUserFromChart(SnakesProto.GamePlayer userToRemove) {
        rating.remove(userToRemove);
    }
}
