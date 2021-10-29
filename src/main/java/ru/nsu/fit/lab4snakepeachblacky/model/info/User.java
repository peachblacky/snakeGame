package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.net.PlayerRoles;

public class User {
    private final String username;
    private final Integer points;
    private PlayerRoles role;

    public String getUsername() {
        return username;
    }

    public Integer getPoints() {
        return points;
    }

    public void setRole(PlayerRoles role) {
        this.role = role;
    }

    public PlayerRoles getRole() {
        return role;
    }

    public User(String username, Integer points, PlayerRoles role) {
        this.username = username;
        this.points = points;
        this.role = role;
    }
}
