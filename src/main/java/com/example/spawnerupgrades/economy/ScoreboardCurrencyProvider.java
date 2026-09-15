package com.example.spawnerupgrades.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Uses a plain vanilla scoreboard objective as the currency (e.g. the "gems"
 * objective set up with /scoreboard objectives add gems dummy "Gems").
 * No extra plugin required besides the server itself.
 */
public class ScoreboardCurrencyProvider implements CurrencyProvider {

    private final String objectiveName;
    private final String displayName;

    public ScoreboardCurrencyProvider(String objectiveName, String displayName) {
        this.objectiveName = objectiveName;
        this.displayName = displayName;
    }

    private Scoreboard board() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }

    private Objective objective() {
        Objective obj = board().getObjective(objectiveName);
        if (obj == null) {
            // Auto-create it if it's missing so server owners don't have to
            // remember the exact vanilla command.
            obj = board().registerNewObjective(objectiveName, "dummy", objectiveName);
        }
        return obj;
    }

    @Override
    public double getBalance(Player player) {
        Score score = objective().getScore(player.getName());
        return score.getScore();
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        Score score = objective().getScore(player.getName());
        int current = score.getScore();
        int cost = (int) Math.round(amount);
        if (current < cost) return false;
        score.setScore(current - cost);
        return true;
    }

    @Override
    public String displayName() {
        return displayName;
    }
}
