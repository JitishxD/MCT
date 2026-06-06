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

public class SetHealth implements CommandExecutor, TabCompleter {

    private static final double MIN_HEALTH = 0;
    private static final double MAX_HEALTH = 20;

    private static boolean isValidHealth(double health) {
        return health >= MIN_HEALTH && health <= MAX_HEALTH;
    }

    private static Double parseHealth(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void applyHealth(Player target, double health) {
        target.setHealth(health);
        target.sendMessage(ChatColor.GREEN + "Your health has been set to " + health);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (!executor.hasPermission("MCT.setHealth")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            executor.sendMessage(ChatColor.RED + "Usage: /sethealth <0-20> | /sethealth <player|all> <0-20>");
            return true;
        }

        if (args.length == 1) {
            Double health = parseHealth(args[0]);
            if (health == null) {
                executor.sendMessage(ChatColor.RED + "Please enter a valid number!");
                return true;
            }
            if (!isValidHealth(health)) {
                executor.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
                return true;
            }
            applyHealth(executor, health);
            return true;
        }

        String targetArg = args[0];
        Double health = parseHealth(args[1]);
        if (health == null) {
            executor.sendMessage(ChatColor.RED + "Please enter a valid number!");
            return true;
        }
        if (!isValidHealth(health)) {
            executor.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
            return true;
        }

        boolean targetingOthers = targetArg.equalsIgnoreCase("all")
                || !targetArg.equalsIgnoreCase(executor.getName());

        if (targetingOthers && !executor.hasPermission("MCT.setHealth.toOtherPlayers")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to set health for other players."));
            return true;
        }

        if (targetArg.equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyHealth(player, health);
            }
            executor.sendMessage(ChatColor.GREEN + "You set health to " + health + " for all players on the server!");
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetArg);
        if (target == null) {
            executor.sendMessage(ChatColor.RED + targetArg + ChatColor.WHITE + " is not online.");
            return true;
        }

        applyHealth(target, health);
        if (!target.equals(executor)) {
            executor.sendMessage(ChatColor.GREEN + "You set health to " + health + " for " + target.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return Collections.emptyList();
        }

        if (args.length != 1 || !sender.hasPermission("MCT.setHealth.toOtherPlayers")) {
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
