package com.example.spawnerupgrades.economy;

import org.bukkit.entity.Player;

/**
 * Abstraction over "whatever currency the server wants to charge for upgrades".
 * Swapping currencies is just a matter of changing currency.mode in config.yml,
 * nothing else in the plugin needs to change.
 */
public interface CurrencyProvider {

    /**
     * @return the player's current balance, or 0 if it can't be resolved.
     */
    double getBalance(Player player);

    /**
     * Attempts to withdraw the given amount from the player.
     * @return true if the withdrawal succeeded (player had enough and the
     *         underlying plugin confirmed it), false otherwise.
     */
    boolean withdraw(Player player, double amount);

    /**
     * A short display name for messages, e.g. "Gems" or "Coins".
     */
    String displayName();
}
