package io.github.unbrokenhunter.tinyserver.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeManager {
    private final Map<UUID, Integer> usedSeconds = new HashMap<>();
    private final Map<UUID, Integer> customLimits = new HashMap<>();
    private final Map<UUID, Integer> bonusSeconds = new HashMap<>();
    private final JavaPlugin plugin;

    public TimeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public int getUsedSeconds(UUID uuid) {
        return usedSeconds.getOrDefault(uuid, 0);
    }

    public int getUsedSeconds(OfflinePlayer player) {
        return getUsedSeconds(player.getUniqueId());
    }

    public void setUsedSeconds(UUID uuid, int seconds) {
        usedSeconds.put(uuid, Math.max(0, seconds));
    }

    public void addUsedSeconds(UUID uuid, int amount) {
        setUsedSeconds(uuid, getUsedSeconds(uuid) + amount);
    }

    public void resetPlayerUsedTime(UUID uuid) {
        usedSeconds.remove(uuid);
    }

    public void resetAllUsedTimes() {
        usedSeconds.clear();
    }

    public void setPlayerLimit(UUID uuid, int seconds) {
        customLimits.put(uuid, Math.max(1, seconds));
    }

    public void clearPlayerLimit(UUID uuid) {
        customLimits.remove(uuid);
    }

    public boolean hasPlayerLimit(UUID uuid) {
        return customLimits.containsKey(uuid);
    }

    public int getDailyLimitSeconds() {
        return plugin.getConfig().getInt("daily-limit-seconds", 900);
    }

    public void setDailyLimitSeconds(int seconds) {
        plugin.getConfig().set("daily-limit-seconds", Math.max(1, seconds));
        plugin.saveConfig();
    }

    public int getBaseLimitForPlayer(UUID uuid) {
        return customLimits.getOrDefault(uuid, getDailyLimitSeconds());
    }

    public int getBonusSeconds(UUID uuid) {
        return bonusSeconds.getOrDefault(uuid, 0);
    }

    public void addBonusSeconds(UUID uuid, int amount) {
        int newValue = getBonusSeconds(uuid) + amount;

        if (newValue <= 0) {
            bonusSeconds.remove(uuid);
            return;
        }

        bonusSeconds.put(uuid, newValue);
    }

    public void clearBonusSeconds(UUID uuid) {
        bonusSeconds.remove(uuid);
    }

    public void clearAllBonusSeconds() {
        bonusSeconds.clear();
    }

    public int getLimitForPlayer(UUID uuid) {
        return getBaseLimitForPlayer(uuid) + getBonusSeconds(uuid);
    }

    public int getRemainingSeconds(UUID uuid) {
        return Math.max(0, getLimitForPlayer(uuid) - getUsedSeconds(uuid));
    }

    public void resetPlayerDailyState(UUID uuid) {
        resetPlayerUsedTime(uuid);
        clearBonusSeconds(uuid);
    }

    public void resetAllDailyStates() {
        resetAllUsedTimes();
        clearAllBonusSeconds();
    }
}