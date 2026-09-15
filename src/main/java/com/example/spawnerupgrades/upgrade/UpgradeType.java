package com.example.spawnerupgrades.upgrade;

public enum UpgradeType {
    SPAWN_DELAY("spawn_delay"),
    DROP_AMOUNT("drop_amount");

    private final String configKey;

    UpgradeType(String configKey) {
        this.configKey = configKey;
    }

    public String configKey() {
        return configKey;
    }
}
