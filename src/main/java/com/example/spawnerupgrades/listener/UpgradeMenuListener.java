package com.example.spawnerupgrades.listener;

import com.example.spawnerupgrades.SpawnerUpgradesPlugin;
import com.example.spawnerupgrades.economy.CurrencyProvider;
import com.example.spawnerupgrades.gui.UpgradeMenu;
import com.example.spawnerupgrades.upgrade.SpawnerKey;
import com.example.spawnerupgrades.upgrade.UpgradeLevel;
import com.example.spawnerupgrades.upgrade.UpgradeTrack;
import com.example.spawnerupgrades.upgrade.UpgradeType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class UpgradeMenuListener implements Listener {

    private final SpawnerUpgradesPlugin plugin;

    public UpgradeMenuListener(SpawnerUpgradesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String expectedTitle = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("menu-title", "&8Spawner Upgrades"));

        if (event.getView().getTitle() == null || !event.getView().getTitle().equals(expectedTitle)) {
            return;
        }

        // Everything in this menu is decorative/click-only; never let items be taken out.
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String typeName = pdc.get(UpgradeMenu.KEY_UPGRADE_TYPE, org.bukkit.persistence.PersistentDataType.STRING);
        String worldName = pdc.get(UpgradeMenu.KEY_WORLD, org.bukkit.persistence.PersistentDataType.STRING);
        Integer x = pdc.get(UpgradeMenu.KEY_X, org.bukkit.persistence.PersistentDataType.INTEGER);
        Integer y = pdc.get(UpgradeMenu.KEY_Y, org.bukkit.persistence.PersistentDataType.INTEGER);
        Integer z = pdc.get(UpgradeMenu.KEY_Z, org.bukkit.persistence.PersistentDataType.INTEGER);

        if (typeName == null || worldName == null || x == null || y == null || z == null) return;

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return;

        Location spawnerLoc = new Location(world, x, y, z);
        UpgradeType type = UpgradeType.valueOf(typeName);
        Player player = (Player) event.getWhoClicked();

        purchase(player, type, spawnerLoc);
    }

    private void purchase(Player player, UpgradeType type, Location spawnerLoc) {
        UpgradeTrack track = plugin.getUpgradeConfig().getTrack(type);
        if (track == null) return;

        SpawnerKey key = SpawnerKey.of(spawnerLoc);
        int currentLevel = plugin.getUpgradeStorage().getLevel(key, type);
        UpgradeLevel next = track.getNextLevel(currentLevel);

        if (next == null) {
            player.sendMessage(plugin.msg("max-level"));
            return;
        }

        CurrencyProvider currency = plugin.getEconomyManager().getProvider();
        double balance = currency.getBalance(player);

        if (balance < next.getCost()) {
            double missing = next.getCost() - balance;
            player.sendMessage(plugin.msg("not-enough-currency")
                    .replace("%cost%", String.valueOf((long) missing))
                    .replace("%currency%", currency.displayName()));
            return;
        }

        boolean withdrawn = currency.withdraw(player, next.getCost());
        if (!withdrawn) {
            player.sendMessage(plugin.msg("not-enough-currency")
                    .replace("%cost%", String.valueOf((long) next.getCost()))
                    .replace("%currency%", currency.displayName()));
            return;
        }

        // Persist the new level, then tell SmartSpawner about it.
        plugin.getUpgradeStorage().setLevel(key, type, next.getLevel());
        plugin.getUpgradeApplier().apply(type, spawnerLoc, next.getValue());

        player.sendMessage(plugin.msg("purchased")
                .replace("%upgrade%", track.getDisplayName())
                .replace("%level%", String.valueOf(next.getLevel())));

        // Refresh the menu in place so the lore/cost updates immediately.
        UpgradeMenu.open(plugin, player, spawnerLoc);
    }
}
