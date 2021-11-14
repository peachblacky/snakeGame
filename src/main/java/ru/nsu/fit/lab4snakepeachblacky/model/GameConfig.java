package ru.nsu.fit.lab4snakepeachblacky.model;

public class GameConfig {
    private String masterName;
    private String masterIp;
    private final Integer columns;
    private final Integer rows;
    private final Integer food_static;
    private final Integer food_dynamic;
    private final Integer state_delay_ms;
    private final Integer ping_delay;
    private final Integer node_timeout_ms;

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterName() {
        return masterName;
    }

    public Integer getColumns() {
        return columns;
    }

    public Integer getRows() {
        return rows;
    }

    public Integer getFood_static() {
        return food_static;
    }

    public Integer getFood_dynamic() {
        return food_dynamic;
    }

    public Integer getState_delay_ms() {
        return state_delay_ms;
    }

    public Integer getPing_delay() {
        return ping_delay;
    }

    public Integer getNode_timeout_ms() {
        return node_timeout_ms;
    }

    public GameConfig(
            Integer columns,
            Integer rows,
            Integer food_static,
            Integer food_dynamic,
            Integer state_delay_ms,
            Integer ping_delay,
            Integer node_timeout_ms) {
        this.masterName = "Unknown";
        this.columns = columns;
        this.rows = rows;
        this.food_static = food_static;
        this.food_dynamic = food_dynamic;
        this.state_delay_ms = state_delay_ms;
        this.ping_delay = ping_delay;
        this.node_timeout_ms = node_timeout_ms;
    }
}
