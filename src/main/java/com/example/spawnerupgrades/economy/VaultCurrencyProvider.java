package com.example.spawnerupgrades.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Charges the server's normal Vault-linked economy (e.g. EssentialsX money).
 * Requires the Vault plugin and a Vault-compatible economy plugin to be installed.
 */
public class VaultCurrencyProvider implements CurrencyProvider {

    private final Economy economy;
    private final String displayName;

    public VaultCurrencyProvider(JavaPlugin plugin, String displayName) {
        this.displayName = displayName;
        RegisteredServiceProvider<Economy> rsp = plugin.getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        this.economy = rsp == null ? null : rsp.getProvider();
        if (this.economy == null) {
            plugin.getLogger().warning("Currency mode is 'vault' but no Vault economy plugin was found! " +
                    "Install Vault + an economy plugin (e.g. EssentialsX), or switch currency.mode in config.yml.");
        }
    }

    @Override
    public double getBalance(Player player) {
        return economy == null ? 0 : economy.getBalance(player);
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (economy == null) return false;
        if (economy.getBalance(player) < amount) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    @Override
    public String displayName() {
        return displayName;
    }
}
