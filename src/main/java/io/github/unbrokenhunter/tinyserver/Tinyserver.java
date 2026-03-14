package io.github.unbrokenhunter.tinyserver;

import io.github.unbrokenhunter.tinyserver.command.TimeLimitCommand;
import io.github.unbrokenhunter.tinyserver.command.TimeLimitTabCompleter;
import io.github.unbrokenhunter.tinyserver.manager.TimeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public final class Tinyserver extends JavaPlugin {

    private TimeManager timeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Tinyserver enabled!");

        timeManager = new TimeManager(this);

        if (getCommand("timelimit") != null) {
            getCommand("timelimit").setExecutor(new TimeLimitCommand(timeManager));
            getCommand("timelimit").setTabCompleter(new TimeLimitTabCompleter());
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            int dailyLimitSeconds = timeManager.getDailyLimitSeconds();

            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();

                timeManager.addUsedSeconds(uuid, 1);

                if (timeManager.getUsedSeconds(uuid) >= dailyLimitSeconds) {
                    Component component = text()
                            .content("You have used your time for today.")
                            .color(color(0x13f832))
                            .build();
                    player.kick(component);
                }
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {
        getLogger().info("Tinyserver disabled");
    }
}