package com.example.spawnerupgrades;

import com.example.spawnerupgrades.command.SpawnerUpgradesCommand;
import com.example.spawnerupgrades.economy.EconomyManager;
import com.example.spawnerupgrades.listener.SpawnerTargetListener;
import com.example.spawnerupgrades.listener.UpgradeMenuListener;
import com.example.spawnerupgrades.upgrade.UpgradeApplier;
import com.example.spawnerupgrades.upgrade.UpgradeConfig;
import com.example.spawnerupgrades.upgrade.UpgradeStorage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerUpgradesPlugin extends JavaPlugin {

    private static SpawnerUpgradesPlugin instance;

    private EconomyManager economyManager;
    private UpgradeConfig upgradeConfig;
    private UpgradeStorage upgradeStorage;
    private UpgradeApplier upgradeApplier;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("SmartSpawner") == null) {
            getLogger().warning("SmartSpawner was not found! This addon will still load, but " +
                    "the upgrade commands won't do anything until SmartSpawner is installed.");
        }

        this.economyManager = new EconomyManager(this);
        this.upgradeConfig = new UpgradeConfig(this);
        this.upgradeStorage = new UpgradeStorage(this);
        this.upgradeApplier = new UpgradeApplier(this);

        getServer().getPluginManager().registerEvents(new SpawnerTargetListener(this), this);
        getServer().getPluginManager().registerEvents(new UpgradeMenuListener(this), this);

        var cmd = getCommand("spawnerupgrades");
        if (cmd != null) {
            cmd.setExecutor(new SpawnerUpgradesCommand(this));
        }

        getLogger().info("SpawnerUpgrades enabled. Shift + right-click a spawner in-game to open the upgrade menu.");
    }

    public void reloadPlugin() {
        reloadConfig();
        economyManager.load();
        upgradeConfig.load(this);
    }

    public String msg(String key) {
        String raw = getConfig().getString("messages." + key, key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public static SpawnerUpgradesPlugin getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public UpgradeConfig getUpgradeConfig() {
        return upgradeConfig;
    }

    public UpgradeStorage getUpgradeStorage() {
        return upgradeStorage;
    }

    public UpgradeApplier getUpgradeApplier() {
        return upgradeApplier;
    }
}
