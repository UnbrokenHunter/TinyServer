package io.github.unbrokenhunter.tinyserver.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeManager {
    private final Map<UUID, Integer> usedSeconds = new HashMap<>();
    private final Map<UUID, Integer> customLimits = new HashMap<>();
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
        int newValue = getUsedSeconds(uuid) + amount;
        setUsedSeconds(uuid, newValue);
    }

    public void resetPlayer(UUID uuid) {
        usedSeconds.put(uuid, 0);
    }

    public void resetAll() {
        usedSeconds.clear();
    }

    public void setPlayerLimit(UUID uuid, int seconds) {
        customLimits.put(uuid, seconds);
    }

    public void clearPlayerLimit(UUID uuid) {
        customLimits.remove(uuid);
    }

    public boolean hasPlayerLimit(UUID uuid) {
        return customLimits.containsKey(uuid);
    }

    public int getLimitForPlayer(UUID uuid) {
        return customLimits.getOrDefault(uuid, getDailyLimitSeconds());
    }

    public int getDailyLimitSeconds() {
        return plugin.getConfig().getInt("daily-limit-seconds", 900);
    }

    public void setDailyLimitSeconds(int seconds) {
        plugin.getConfig().set("daily-limit-seconds", seconds);
        plugin.saveConfig();
    }

    public int getRemainingSeconds(UUID uuid) {
        return Math.max(0, getLimitForPlayer(uuid) - getUsedSeconds(uuid));
    }
}