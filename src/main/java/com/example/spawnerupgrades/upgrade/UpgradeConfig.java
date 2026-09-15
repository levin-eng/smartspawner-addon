package com.example.spawnerupgrades.upgrade;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class UpgradeConfig {

    private final Map<UpgradeType, UpgradeTrack> tracks = new EnumMap<>(UpgradeType.class);

    public UpgradeConfig(JavaPlugin plugin) {
        load(plugin);
    }

    public void load(JavaPlugin plugin) {
        tracks.clear();
        ConfigurationSection upgradesSection = plugin.getConfig().getConfigurationSection("upgrades");
        if (upgradesSection == null) {
            plugin.getLogger().warning("No 'upgrades' section found in config.yml!");
            return;
        }

        for (UpgradeType type : UpgradeType.values()) {
            ConfigurationSection section = upgradesSection.getConfigurationSection(type.configKey());
            if (section == null) {
                plugin.getLogger().warning("Missing config for upgrade '" + type.configKey() + "', skipping.");
                continue;
            }

            String displayName = section.getString("display-name", type.configKey());
            String description = section.getString("description", "");
            List<UpgradeLevel> levels = new ArrayList<>();

            List<Map<?, ?>> levelMaps = section.getMapList("levels");
            for (Map<?, ?> map : levelMaps) {
                int level = toInt(map.get("level"));
                double cost = toDouble(map.get("cost"));
                int value = toInt(map.get("value"));
                levels.add(new UpgradeLevel(level, cost, value));
            }
            levels.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));

            tracks.put(type, new UpgradeTrack(type, displayName, description, levels));
        }
    }

    private int toInt(Object o) {
        return o instanceof Number ? ((Number) o).intValue() : 0;
    }

    private double toDouble(Object o) {
        return o instanceof Number ? ((Number) o).doubleValue() : 0;
    }

    public UpgradeTrack getTrack(UpgradeType type) {
        return tracks.get(type);
    }

    public Map<UpgradeType, UpgradeTrack> getAllTracks() {
        return tracks;
    }
}
