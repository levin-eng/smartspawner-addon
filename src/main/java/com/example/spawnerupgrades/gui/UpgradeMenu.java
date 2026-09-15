package com.example.spawnerupgrades.gui;

import com.example.spawnerupgrades.SpawnerUpgradesPlugin;
import com.example.spawnerupgrades.economy.CurrencyProvider;
import com.example.spawnerupgrades.upgrade.SpawnerKey;
import com.example.spawnerupgrades.upgrade.UpgradeLevel;
import com.example.spawnerupgrades.upgrade.UpgradeType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds and opens the upgrade GUI for a specific spawner.
 * Slot 11 = Spawn Rate upgrade button, Slot 15 = Drop Amount upgrade button.
 * Custom items carry the spawner location + upgrade type in their
 * PersistentDataContainer so the click listener can read them back without
 * needing any separate "currently open menu" tracking.
 */
public class UpgradeMenu {

    public static final NamespacedKey KEY_UPGRADE_TYPE;
    public static final NamespacedKey KEY_WORLD;
    public static final NamespacedKey KEY_X;
    public static final NamespacedKey KEY_Y;
    public static final NamespacedKey KEY_Z;

    static {
        KEY_UPGRADE_TYPE = new NamespacedKey(SpawnerUpgradesPlugin.getInstance(), "upgrade_type");
        KEY_WORLD = new NamespacedKey(SpawnerUpgradesPlugin.getInstance(), "spawner_world");
        KEY_X = new NamespacedKey(SpawnerUpgradesPlugin.getInstance(), "spawner_x");
        KEY_Y = new NamespacedKey(SpawnerUpgradesPlugin.getInstance(), "spawner_y");
        KEY_Z = new NamespacedKey(SpawnerUpgradesPlugin.getInstance(), "spawner_z");
    }

    public static void open(SpawnerUpgradesPlugin plugin, Player player, Location spawnerLoc) {
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("menu-title", "&8Spawner Upgrades"));
        Inventory inv = plugin.getServer().createInventory(null, 27, title);

        SpawnerKey key = SpawnerKey.of(spawnerLoc);
        CurrencyProvider currency = plugin.getEconomyManager().getProvider();

        inv.setItem(11, buildButton(plugin, UpgradeType.SPAWN_DELAY, key, spawnerLoc, currency, player, Material.CLOCK));
        inv.setItem(15, buildButton(plugin, UpgradeType.DROP_AMOUNT, key, spawnerLoc, currency, player, Material.CHEST));

        player.openInventory(inv);
    }

    private static ItemStack buildButton(SpawnerUpgradesPlugin plugin, UpgradeType type, SpawnerKey key,
                                          Location loc, CurrencyProvider currency, Player player, Material icon) {
        var track = plugin.getUpgradeConfig().getTrack(type);
        int currentLevel = plugin.getUpgradeStorage().getLevel(key, type);
        UpgradeLevel next = track == null ? null : track.getNextLevel(currentLevel);

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();

        String name = track == null ? type.name() : track.getDisplayName();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&l" + name));

        List<String> lore = new ArrayList<>();
        if (track != null) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + track.getDescription()));
        }
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Current level: &f" + currentLevel +
                (track != null ? "&7/&f" + track.getMaxLevel() : "")));

        if (next == null) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&eMax level reached!"));
        } else {
            double balance = currency.getBalance(player);
            boolean canAfford = balance >= next.getCost();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Next level: &f" + next.getLevel()));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: " + (canAfford ? "&a" : "&c")
                    + (long) next.getCost() + " " + currency.displayName()));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    canAfford ? "&a&lClick to purchase!" : "&c&lNot enough " + currency.displayName() + "!"));
        }

        meta.setLore(lore);

        var pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_UPGRADE_TYPE, PersistentDataType.STRING, type.name());
        pdc.set(KEY_WORLD, PersistentDataType.STRING, loc.getWorld().getName());
        pdc.set(KEY_X, PersistentDataType.INTEGER, loc.getBlockX());
        pdc.set(KEY_Y, PersistentDataType.INTEGER, loc.getBlockY());
        pdc.set(KEY_Z, PersistentDataType.INTEGER, loc.getBlockZ());

        item.setItemMeta(meta);
        return item;
    }
}
