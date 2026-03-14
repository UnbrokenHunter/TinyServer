package io.github.unbrokenhunter.tinyserver.ui;

import io.github.unbrokenhunter.tinyserver.manager.TimeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class TabDisplayManager {

    private final TimeManager timeManager;

    public TabDisplayManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public void updatePlayerTabName(Player player) {
        int remaining = timeManager.getRemainingSeconds(player.getUniqueId());

        int minutes = remaining / 60;
        int seconds = remaining % 60;

        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        Component tabName = text()
                .content(player.getName() + " [" + formattedTime + "]")
                .color(color(0x13f832))
                .build();

        player.playerListName(tabName);
    }
}