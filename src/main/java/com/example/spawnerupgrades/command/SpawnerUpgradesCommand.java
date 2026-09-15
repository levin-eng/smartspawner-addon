package com.example.spawnerupgrades.command;

import com.example.spawnerupgrades.SpawnerUpgradesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpawnerUpgradesCommand implements CommandExecutor {

    private final SpawnerUpgradesPlugin plugin;

    public SpawnerUpgradesCommand(SpawnerUpgradesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spawnerupgrades.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Usage: /" + label + " reload");
            return true;
        }

        plugin.reloadPlugin();
        sender.sendMessage(plugin.msg("reloaded"));
        return true;
    }
}
