package com.example.spawnerupgrades.upgrade;

public class UpgradeLevel {
    private final int level;
    private final double cost;
    private final int value;

    public UpgradeLevel(int level, double cost, int value) {
        this.level = level;
        this.cost = cost;
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public double getCost() {
        return cost;
    }

    public int getValue() {
        return value;
    }
}
