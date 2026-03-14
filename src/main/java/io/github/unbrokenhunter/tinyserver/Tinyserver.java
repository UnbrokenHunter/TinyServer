package io.github.unbrokenhunter.tinyserver;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public final class Tinyserver extends JavaPlugin implements CommandExecutor, TabCompleter {

    private final Map<UUID, Integer> usedSeconds = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Tinyserver enabled!");

        if (getCommand("timelimit") != null) {
            getCommand("timelimit").setExecutor(this);
            getCommand("timelimit").setTabCompleter(this);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            int dailyLimitSeconds = getConfig().getInt("daily-limit-seconds", 900);

            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();

                int newTime = usedSeconds.getOrDefault(uuid, 0) + 1;
                usedSeconds.put(uuid, newTime);

                if (newTime >= dailyLimitSeconds) {
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tinyserver.timelimit")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage:");
            sender.sendMessage("/timelimit check <player>");
            sender.sendMessage("/timelimit reset <player>");
            sender.sendMessage("/timelimit add <player> <seconds>");
            sender.sendMessage("/timelimit setlimit <seconds>");
            sender.sendMessage("/timelimit globalreset");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "check" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /timelimit check <player>");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                UUID uuid = target.getUniqueId();

                int used = usedSeconds.getOrDefault(uuid, 0);
                int limit = getConfig().getInt("daily-limit-seconds", 900);
                int remaining = Math.max(0, limit - used);

                String targetName = target.getName() != null ? target.getName() : args[1];
                sender.sendMessage(targetName + " has used " + used + " seconds today.");
                sender.sendMessage(targetName + " has " + remaining + " seconds remaining.");
            }

            case "reset" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /timelimit reset <player>");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                UUID uuid = target.getUniqueId();

                usedSeconds.put(uuid, 0);

                String targetName = target.getName() != null ? target.getName() : args[1];
                sender.sendMessage("Reset " + targetName + "'s timer.");
            }

            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /timelimit add <player> <seconds>");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                UUID uuid = target.getUniqueId();

                try {
                    int amount = Integer.parseInt(args[2]);
                    int newValue = usedSeconds.getOrDefault(uuid, 0) + amount;
                    usedSeconds.put(uuid, Math.max(0, newValue));

                    String targetName = target.getName() != null ? target.getName() : args[1];
                    sender.sendMessage("Added " + amount + " seconds to " + targetName + ".");
                } catch (NumberFormatException e) {
                    sender.sendMessage("Seconds must be a number.");
                }
            }

            case "setlimit" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /timelimit setlimit <seconds>");
                    return true;
                }

                try {
                    int newLimit = Integer.parseInt(args[1]);

                    if (newLimit < 1) {
                        sender.sendMessage("Limit must be at least 1 second.");
                        return true;
                    }

                    getConfig().set("daily-limit-seconds", newLimit);
                    saveConfig();

                    sender.sendMessage("Global daily limit set to " + newLimit + " seconds.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("Seconds must be a number.");
                }
            }

            case "globalreset" -> {
                usedSeconds.clear();
                sender.sendMessage("Reset all tracked player times.");
            }

            default -> sender.sendMessage("Unknown subcommand.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("tinyserver.timelimit")) {
            return completions;
        }

        if (args.length == 1) {
            List<String> subcommands = List.of("check", "reset", "add", "setlimit", "globalreset");
            StringUtil.copyPartialMatches(args[0], subcommands, completions);
            return completions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("check") || subcommand.equals("reset") || subcommand.equals("add")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[1], playerNames, completions);
                return completions;
            }

            if (subcommand.equals("setlimit")) {
                List<String> commonValues = List.of("60", "300", "600", "900", "1800");
                StringUtil.copyPartialMatches(args[1], commonValues, completions);
                return completions;
            }
        }

        if (args.length == 3) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("add")) {
                List<String> commonValues = List.of("30", "60", "120", "300");
                StringUtil.copyPartialMatches(args[2], commonValues, completions);
                return completions;
            }
        }

        return completions;
    }
}