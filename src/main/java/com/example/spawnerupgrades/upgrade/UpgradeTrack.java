package com.example.spawnerupgrades.upgrade;

import java.util.List;

public class UpgradeTrack {
    private final UpgradeType type;
    private final String displayName;
    private final String description;
    private final List<UpgradeLevel> levels; // sorted ascending by level

    public UpgradeTrack(UpgradeType type, String displayName, String description, List<UpgradeLevel> levels) {
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.levels = levels;
    }

    public UpgradeType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxLevel() {
        return levels.size();
    }

    /**
     * @param currentLevel 0 means "not upgraded yet"
     * @return the next level to purchase, or null if already maxed out
     */
    public UpgradeLevel getNextLevel(int currentLevel) {
        if (currentLevel >= levels.size()) return null;
        return levels.get(currentLevel); // levels list is 0-indexed, level 1 is at index 0
    }

    public UpgradeLevel getLevel(int level) {
        if (level <= 0 || level > levels.size()) return null;
        return levels.get(level - 1);
    }
}
