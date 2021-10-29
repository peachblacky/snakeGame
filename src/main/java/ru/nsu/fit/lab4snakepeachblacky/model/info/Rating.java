package ru.nsu.fit.lab4snakepeachblacky.model.info;

import java.util.ArrayList;
import java.util.List;

public class Rating {
    private List<User> rating;

    public Rating() {
        this.rating = new ArrayList<>();
    }

    public void addUserToChart(User newUser) {
        if(!rating.contains(newUser)) {
            rating.add(newUser);
        }
    }

    public void removeUserFromChart(User userToRemove) {
        rating.remove(userToRemove);
    }
}
