package com.example.spawnerupgrades.upgrade;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores, per spawner, what level each upgrade track is currently at.
 * Backed by a single data.yml so it survives restarts. Keyed by the
 * spawner's block location (see SpawnerKey).
 */
public class UpgradeStorage {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;

    // spawnerKey -> (upgradeType -> level)
    private final Map<String, Map<UpgradeType, Integer>> data = new HashMap<>();

    public UpgradeStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        yaml = YamlConfiguration.loadConfiguration(file);
        data.clear();

        for (String spawnerKeyStr : yaml.getKeys(false)) {
            Map<UpgradeType, Integer> levels = new HashMap<>();
            for (UpgradeType type : UpgradeType.values()) {
                int level = yaml.getInt(spawnerKeyStr + "." + type.configKey(), 0);
                if (level > 0) levels.put(type, level);
            }
            data.put(spawnerKeyStr, levels);
        }
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public int getLevel(SpawnerKey key, UpgradeType type) {
        Map<UpgradeType, Integer> levels = data.get(key.serialize());
        if (levels == null) return 0;
        return levels.getOrDefault(type, 0);
    }

    public void setLevel(SpawnerKey key, UpgradeType type, int level) {
        data.computeIfAbsent(key.serialize(), k -> new HashMap<>()).put(type, level);
        yaml.set(key.serialize() + "." + type.configKey(), level);
        save();
    }
}
