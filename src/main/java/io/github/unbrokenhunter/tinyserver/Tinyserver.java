package io.github.unbrokenhunter.tinyserver;

import io.github.unbrokenhunter.tinyserver.command.TimeLimitCommand;
import io.github.unbrokenhunter.tinyserver.command.TimeLimitTabCompleter;
import io.github.unbrokenhunter.tinyserver.manager.TimeManager;
import io.github.unbrokenhunter.tinyserver.ui.TabDisplayManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public final class Tinyserver extends JavaPlugin {

    private TimeManager timeManager;
    private TabDisplayManager tabDisplayManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Tinyserver enabled!");

        timeManager = new TimeManager(this);
        tabDisplayManager = new TabDisplayManager(timeManager);

        if (getCommand("timelimit") != null) {
            getCommand("timelimit").setExecutor(new TimeLimitCommand(timeManager));
            getCommand("timelimit").setTabCompleter(new TimeLimitTabCompleter());
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                int playerLimit = timeManager.getLimitForPlayer(uuid);

                timeManager.addUsedSeconds(uuid, 1);
                tabDisplayManager.updatePlayerTabName(player);

                if (timeManager.getUsedSeconds(uuid) >= playerLimit) {
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
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playerListName(Component.text(player.getName()));
        }

        getLogger().info("Tinyserver disabled");
    }
}
