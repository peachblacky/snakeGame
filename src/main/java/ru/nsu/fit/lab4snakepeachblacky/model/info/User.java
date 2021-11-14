package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.net.PlayerRole;

public class User {
    private final String username;
    private final Integer snakeNumber;
    private PlayerRole role;
    private final String idAddress;
    private final String port;


    public String getUsername() {
        return username;
    }

    public Integer getSnakeNumber() {
        return snakeNumber;
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    public PlayerRole getRole() {
        return role;
    }

    public User(String username, Integer snakeNumber, PlayerRole role, String idAddress, String port) {
        this.username = username;
        this.snakeNumber = snakeNumber;
        this.role = role;
        this.idAddress = idAddress;
        this.port = port;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public String getPort() {
        return port;
    }
}
