package com.example.spawnerupgrades.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Applies a purchased upgrade level to the actual spawner by running
 * SmartSpawner's own admin command as console. This is deliberately
 * command-based rather than calling SmartSpawner's internal API directly:
 * SmartSpawner's internal spawner-data class isn't a stable public API and
 * changes between versions, but its admin command is a supported, versioned
 * interface meant for exactly this kind of external control.
 *
 * IMPORTANT: the exact command name/argument order depends on your installed
 * SmartSpawner version. Run "/ss admin help" in-game to confirm it, then set
 * apply.spawn-delay-command / apply.drop-amount-command in config.yml.
 * Leave apply.dry-run: true until you've verified it actually works.
 */
public class UpgradeApplier {

    private final JavaPlugin plugin;

    public UpgradeApplier(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void apply(UpgradeType type, Location spawnerLoc, int value) {
        String template;
        switch (type) {
            case SPAWN_DELAY:
                template = plugin.getConfig().getString("apply.spawn-delay-command");
                break;
            case DROP_AMOUNT:
                template = plugin.getConfig().getString("apply.drop-amount-command");
                break;
            default:
                return;
        }

        if (template == null || template.isBlank()) {
            plugin.getLogger().warning("No apply command configured for " + type + "!");
            return;
        }

        String command = template
                .replace("%world%", spawnerLoc.getWorld().getName())
                .replace("%x%", String.valueOf(spawnerLoc.getBlockX()))
                .replace("%y%", String.valueOf(spawnerLoc.getBlockY()))
                .replace("%z%", String.valueOf(spawnerLoc.getBlockZ()))
                .replace("%value%", String.valueOf(value));

        boolean dryRun = plugin.getConfig().getBoolean("apply.dry-run", true);
        if (dryRun) {
            plugin.getLogger().info("[DRY RUN] Would run: /" + command);
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
