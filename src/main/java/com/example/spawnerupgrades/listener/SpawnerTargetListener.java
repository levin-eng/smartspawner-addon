package com.example.spawnerupgrades.listener;

import com.example.spawnerupgrades.SpawnerUpgradesPlugin;
import com.example.spawnerupgrades.gui.UpgradeMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Shift + right-click a spawner block -> opens the Upgrade menu.
 * A plain right-click is left completely alone, so SmartSpawner's own menu
 * still opens exactly as it always has.
 */
public class SpawnerTargetListener implements Listener {

    private final SpawnerUpgradesPlugin plugin;

    public SpawnerTargetListener(SpawnerUpgradesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return; // plain right-click stays untouched

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SPAWNER) return;

        if (!player.hasPermission("spawnerupgrades.use")) {
            player.sendMessage(plugin.msg("no-permission"));
            return;
        }

        // Prevent this also triggering SmartSpawner's own GUI on the same click.
        event.setCancelled(true);

        UpgradeMenu.open(plugin, player, block.getLocation());
    }
}
