package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetFood implements CommandExecutor, TabCompleter {

    private static final int MIN_FOOD = 0;
    private static final int MAX_FOOD = 20;

    private static boolean isValidFoodLevel(int foodLevel) {
        return foodLevel >= MIN_FOOD && foodLevel <= MAX_FOOD;
    }

    private static Integer parseFoodLevel(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void applyFood(Player target, int foodLevel) {
        target.setFoodLevel(foodLevel);
        target.sendMessage(ChatColor.GREEN + "Your food level has been set to " + foodLevel);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!executor.hasPermission("MCT.setFood")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            executor.sendMessage(ChatColor.RED + "Usage: /setfood <0-20> | /setfood <player|all> <0-20>");
            return true;
        }

        if (args.length == 1) {
            Integer foodLevel = parseFoodLevel(args[0]);
            if (foodLevel == null) {
                executor.sendMessage(ChatColor.RED + "Please enter a valid number!");
                return true;
            }
            if (!isValidFoodLevel(foodLevel)) {
                executor.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
                return true;
            }
            applyFood(executor, foodLevel);
            return true;
        }

        String targetArg = args[0];
        Integer foodLevel = parseFoodLevel(args[1]);
        if (foodLevel == null) {
            executor.sendMessage(ChatColor.RED + "Please enter a valid number!");
            return true;
        }
        if (!isValidFoodLevel(foodLevel)) {
            executor.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
            return true;
        }

        boolean targetingOthers = targetArg.equalsIgnoreCase("all")
                || !targetArg.equalsIgnoreCase(executor.getName());

        if (targetingOthers && !executor.hasPermission("MCT.setFood.toOtherPlayers")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to set food level for other players."));
            return true;
        }

        if (targetArg.equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyFood(player, foodLevel);
            }
            executor.sendMessage(ChatColor.GREEN + "You set food level to " + foodLevel + " for all players on the server!");
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetArg);
        if (target == null) {
            executor.sendMessage(ChatColor.RED + targetArg + ChatColor.WHITE + " is not online.");
            return true;
        }

        applyFood(target, foodLevel);
        if (!target.equals(executor)) {
            executor.sendMessage(ChatColor.GREEN + "You set food level to " + foodLevel + " for " + target.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return Collections.emptyList();
        }

        if (args.length != 1 || !sender.hasPermission("MCT.setFood.toOtherPlayers")) {
            return Collections.emptyList();
        }

        List<String> options = new ArrayList<>();
        options.add("all");
        for (Player player : Bukkit.getOnlinePlayers()) {
            options.add(player.getName());
        }

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], options, completions);
        Collections.sort(completions);
        return completions;
    }
}
