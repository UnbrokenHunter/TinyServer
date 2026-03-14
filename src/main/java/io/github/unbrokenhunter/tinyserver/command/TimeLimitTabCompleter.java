package io.github.unbrokenhunter.tinyserver.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TimeLimitTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();

            if (sender.hasPermission("tinyserver.timelimit.check")) {
                subcommands.add("check");
            }

            if (sender.hasPermission("tinyserver.timelimit.admin")) {
                subcommands.add("reset");
                subcommands.add("add");
                subcommands.add("setlimit");
                subcommands.add("clearlimit");
                subcommands.add("globalreset");
            }

            StringUtil.copyPartialMatches(args[0], subcommands, completions);
            return completions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("check")) {
                if (!sender.hasPermission("tinyserver.timelimit.check")) {
                    return completions;
                }
            } else {
                if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                    return completions;
                }
            }

            if (subcommand.equals("check") || subcommand.equals("reset") || subcommand.equals("add") || subcommand.equals("clearlimit")) {                List<String> playerNames = new ArrayList<>();
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
            if (!sender.hasPermission("tinyserver.timelimit.admin")) {
                return completions;
            }

            if (args[0].equalsIgnoreCase("add")) {
                List<String> commonValues = List.of("30", "60", "120", "300");
                StringUtil.copyPartialMatches(args[2], commonValues, completions);
                return completions;
            }

            if (args[0].equalsIgnoreCase("setlimit")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                StringUtil.copyPartialMatches(args[2], playerNames, completions);
                return completions;
            }
        }

        return completions;
    }
}