package com.example.spawnerupgrades.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Reads currency.mode from config.yml and builds the matching CurrencyProvider.
 * This is the ONLY class that needs to know about the different modes; every
 * other part of the plugin just talks to the CurrencyProvider interface.
 */
public class EconomyManager {

    private final JavaPlugin plugin;
    private CurrencyProvider provider;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        FileConfiguration cfg = plugin.getConfig();
        String mode = cfg.getString("currency.mode", "vault").toLowerCase();
        String displayName = cfg.getString("currency.display-name", "Currency");

        switch (mode) {
            case "scoreboard":
                String objective = cfg.getString("currency.scoreboard.objective", "gems");
                provider = new ScoreboardCurrencyProvider(objective, displayName);
                plugin.getLogger().info("Currency mode: scoreboard (objective '" + objective + "')");
                break;

            case "command":
                String balancePlaceholder = cfg.getString("currency.command.balance-placeholder");
                String give = cfg.getString("currency.command.give-command");
                String take = cfg.getString("currency.command.take-command");
                provider = new CommandCurrencyProvider(plugin, balancePlaceholder, give, take, displayName);
                plugin.getLogger().info("Currency mode: command (placeholder '" + balancePlaceholder + "')");
                break;

            case "vault":
            default:
                provider = new VaultCurrencyProvider(plugin, displayName);
                plugin.getLogger().info("Currency mode: vault");
                break;
        }
    }

    public CurrencyProvider getProvider() {
        return provider;
    }
}
