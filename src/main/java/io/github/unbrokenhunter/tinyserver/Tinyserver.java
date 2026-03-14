package io.github.unbrokenhunter.tinyserver;

import org.bukkit.plugin.java.JavaPlugin;

public final class Tinyserver extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MyPlugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyPlugin disabled");
    }
}
