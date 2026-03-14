package io.github.unbrokenhunter.tinyserver.command;

import io.github.unbrokenhunter.tinyserver.manager.TimeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

public class TimeLimitCommand implements CommandExecutor {

    private final TimeManager timeManager;

    public TimeLimitCommand(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    private Component prefix() {
        return text("[TinyServer] ", AQUA).decorate(BOLD);
    }

    private void sendPrefixedMessage(CommandSender sender, Component message) {
        sender.sendMessage(prefix().append(message.decoration(BOLD, FALSE)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendPrefixedMessage(
                    sender,
                    text("Usage: ", YELLOW)
                            .append(text("/timelimit check <player>", GOLD))
                            .append(text(" | ", DARK_GRAY))
                            .append(text("/timelimit reset <player>", GOLD))
                            .append(text(" | ", DARK_GRAY))
                            .append(text("/timelimit add <player> <seconds>", GOLD))
                            .append(text(" | ", DARK_GRAY))
                            .append(text("/timelimit setlimit <seconds> [player]", GOLD))
                            .append(text(" | ", DARK_GRAY))
                            .append(text("/timelimit globalreset", GOLD))
                            .append(text(" | ", DARK_GRAY))
                            .append(text("/timelimit clearlimit <player>", GOLD))
            );
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "check" -> {
                if (!sender.hasPermission("tinyserver.timelimit.check")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                if (args.length < 2) {
                    sendPrefixedMessage(sender, text("Usage: /timelimit check <player>", YELLOW));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                UUID uuid = target.getUniqueId();

                int used = timeManager.getUsedSeconds(uuid);
                int baseLimit = timeManager.getBaseLimitForPlayer(uuid);
                int bonus = timeManager.getBonusSeconds(uuid);
                int effectiveLimit = timeManager.getLimitForPlayer(uuid);
                int remaining = timeManager.getRemainingSeconds(uuid);
                boolean custom = timeManager.hasPlayerLimit(uuid);

                String targetName = target.getName() != null ? target.getName() : args[1];

                sendPrefixedMessage(
                        sender,
                        text(targetName, AQUA)
                                .append(text(" has used ", GRAY))
                                .append(text(used + "s", GREEN))
                                .append(text(", has ", GRAY))
                                .append(text(remaining + "s", GREEN))
                                .append(text(" remaining, base limit: ", GRAY))
                                .append(text(baseLimit + "s", GREEN))
                                .append(text(" (", DARK_GRAY))
                                .append(text(custom ? "personal" : "global", GOLD))
                                .append(text("), bonus today: ", GRAY))
                                .append(text(bonus + "s", GREEN))
                                .append(text(", total today: ", GRAY))
                                .append(text(effectiveLimit + "s", GREEN))
                                .append(text(".", GRAY))
                );
            }

            case "reset" -> {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                if (args.length < 2) {
                    sendPrefixedMessage(sender, text("Usage: /timelimit reset <player>", YELLOW));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                timeManager.resetPlayerUsedTime(target.getUniqueId());
                timeManager.clearBonusSeconds(target.getUniqueId());

                String targetName = target.getName() != null ? target.getName() : args[1];

                sendPrefixedMessage(
                        sender,
                        text("Reset ", GRAY)
                                .append(text(targetName, AQUA))
                                .append(text("'s timer.", GREEN))
                );
            }

            case "add" -> {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                if (args.length < 3) {
                    sendPrefixedMessage(sender, text("Usage: /timelimit add <player> <seconds>", YELLOW));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                try {
                    int amount = Integer.parseInt(args[2]);

                    if (amount < 1) {
                        sendPrefixedMessage(sender, text("Seconds must be a positive number.", RED));
                        return true;
                    }

                    timeManager.addBonusSeconds(target.getUniqueId(), amount);

                    String targetName = target.getName() != null ? target.getName() : args[1];
                    int totalBonus = timeManager.getBonusSeconds(target.getUniqueId());

                    sendPrefixedMessage(
                            sender,
                            text("Added ", GRAY)
                                    .append(text(amount + " seconds", GREEN))
                                    .append(text(" of bonus time for today to ", GRAY))
                                    .append(text(targetName, AQUA))
                                    .append(text(". They now have ", GRAY))
                                    .append(text(totalBonus + " bonus seconds", GREEN))
                                    .append(text(" today.", GRAY))
                    );
                } catch (NumberFormatException e) {
                    sendPrefixedMessage(sender, text("Seconds must be a number.", RED));
                }
            }

            case "setlimit" -> {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                if (args.length < 2) {
                    sendPrefixedMessage(sender, text("Usage: /timelimit setlimit <seconds> [player]", YELLOW));
                    return true;
                }

                try {
                    int newLimit = Integer.parseInt(args[1]);

                    if (newLimit < 1) {
                        sendPrefixedMessage(sender, text("Limit must be at least 1 second.", RED));
                        return true;
                    }

                    if (args.length == 2) {
                        timeManager.setDailyLimitSeconds(newLimit);

                        sendPrefixedMessage(
                                sender,
                                text("Global daily limit set to ", GRAY)
                                        .append(text(newLimit + " seconds", GREEN))
                                        .append(text(".", GRAY))
                        );
                        return true;
                    }

                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    String targetName = target.getName() != null ? target.getName() : args[2];

                    timeManager.setPlayerLimit(target.getUniqueId(), newLimit);

                    sendPrefixedMessage(
                            sender,
                            text("Set ", GRAY)
                                    .append(text(targetName, AQUA))
                                    .append(text("'s personal limit to ", GRAY))
                                    .append(text(newLimit + " seconds", GREEN))
                                    .append(text(".", GRAY))
                    );
                } catch (NumberFormatException e) {
                    sendPrefixedMessage(sender, text("Seconds must be a number.", RED));
                }
            }

            case "clearlimit" -> {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                if (args.length < 2) {
                    sendPrefixedMessage(sender, text("Usage: /timelimit clearlimit <player>", YELLOW));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                String targetName = target.getName() != null ? target.getName() : args[1];

                if (!timeManager.hasPlayerLimit(target.getUniqueId())) {
                    sendPrefixedMessage(
                            sender,
                            text(targetName, AQUA)
                                    .append(text(" does not have a personal limit set.", GRAY))
                    );
                    return true;
                }

                timeManager.clearPlayerLimit(target.getUniqueId());

                sendPrefixedMessage(
                        sender,
                        text("Cleared ", GRAY)
                                .append(text(targetName, AQUA))
                                .append(text("'s personal limit. They now use the global limit.", GREEN))
                );
            }

            case "globalreset" -> {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    sendPrefixedMessage(sender, text("You do not have permission to use this command.", RED));
                    return true;
                }

                timeManager.resetAllUsedTimes();
                timeManager.clearAllBonusSeconds();
                sendPrefixedMessage(sender, text("Reset all tracked player times.", GREEN));
            }

            default -> sendPrefixedMessage(sender, text("Unknown subcommand.", RED));
        }

        return true;
    }
}