package ru.nsu.fit.lab4snakepeachblacky.model.info;

import ru.nsu.fit.lab4snakepeachblacky.net.PlayerRoles;

public class GameInfo {
    private final Integer gameId;
    private User master;
    private final Integer width;
    private final Integer height;
    private final Integer staticFood;
    private final Integer activeFood;

    public void setMaster(User master) {
        master.setRole(PlayerRoles.MASTER);
        this.master = master;
    }

    public User getMaster() {
        return master;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getStaticFood() {
        return staticFood;
    }

    public Integer getActiveFood() {
        return activeFood;
    }

    public GameInfo(Integer gameId, User master, Integer width, Integer height, Integer staticFood, Integer activeFood) {
        this.gameId = gameId;
        this.master = master;
        this.width = width;
        this.height = height;
        this.staticFood = staticFood;
        this.activeFood = activeFood;
    }

}
