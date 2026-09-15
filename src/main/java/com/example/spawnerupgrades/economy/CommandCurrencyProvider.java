package com.example.spawnerupgrades.economy;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Generic currency mode that works with literally any currency plugin:
 * reads the balance through a PlaceholderAPI placeholder, and gives/takes
 * by running console commands. This is how you'd hook up BeastTokens,
 * TokenManager, PlayerPoints, or anything else without this plugin needing
 * to know that plugin's API at all.
 */
public class CommandCurrencyProvider implements CurrencyProvider {

    private final JavaPlugin plugin;
    private final String balancePlaceholder;
    private final String giveCommandTemplate;
    private final String takeCommandTemplate;
    private final String displayName;

    public CommandCurrencyProvider(JavaPlugin plugin, String balancePlaceholder,
                                    String giveCommandTemplate, String takeCommandTemplate,
                                    String displayName) {
        this.plugin = plugin;
        this.balancePlaceholder = balancePlaceholder;
        this.giveCommandTemplate = giveCommandTemplate;
        this.takeCommandTemplate = takeCommandTemplate;
        this.displayName = displayName;

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            plugin.getLogger().warning("Currency mode is 'command' but PlaceholderAPI is not installed! " +
                    "Install PlaceholderAPI, or switch currency.mode in config.yml.");
        }
    }

    @Override
    public double getBalance(Player player) {
        String raw = PlaceholderAPI.setPlaceholders(player, balancePlaceholder);
        try {
            // Strip anything that isn't a digit, dot, or minus sign in case the
            // placeholder returns formatted text like "1,234" or "1.2K".
            String cleaned = raw.replaceAll("[^0-9.\\-]", "");
            return cleaned.isEmpty() ? 0 : Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Could not parse balance from placeholder '" + balancePlaceholder +
                    "' (got: '" + raw + "'). Make sure it returns a plain number.");
            return 0;
        }
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (getBalance(player) < amount) return false;
        String command = takeCommandTemplate
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf((long) amount));
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    /**
     * Not part of the interface, but handy if you ever want an admin command
     * to grant currency for testing.
     */
    public boolean give(Player player, double amount) {
        String command = giveCommandTemplate
                .replace("%player%", player.getName())
                .replace("%amount%", String.valueOf((long) amount));
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public String displayName() {
        return displayName;
    }
}
