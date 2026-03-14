package io.github.unbrokenhunter.tinyserver.command;

import io.github.unbrokenhunter.tinyserver.manager.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class TimeLimitCommand implements CommandExecutor {

    private final TimeManager timeManager;

    public TimeLimitCommand(TimeManager timeManager) {
        this.timeManager = timeManager;
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

                int used = timeManager.getUsedSeconds(uuid);
                int remaining = timeManager.getRemainingSeconds(uuid);

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
                timeManager.resetPlayer(target.getUniqueId());

                String targetName = target.getName() != null ? target.getName() : args[1];
                sender.sendMessage("Reset " + targetName + "'s timer.");
            }

            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage("Usage: /timelimit add <player> <seconds>");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                try {
                    int amount = Integer.parseInt(args[2]);
                    timeManager.addUsedSeconds(target.getUniqueId(), amount);

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

                    timeManager.setDailyLimitSeconds(newLimit);
                    sender.sendMessage("Global daily limit set to " + newLimit + " seconds.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("Seconds must be a number.");
                }
            }

            case "globalreset" -> {
                timeManager.resetAll();
                sender.sendMessage("Reset all tracked player times.");
            }

            default -> sender.sendMessage("Unknown subcommand.");
        }

        return true;
    }
}