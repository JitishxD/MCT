package me.jitish.mCT.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetHealth implements CommandExecutor, TabCompleter {
    private void setPlayerHealth(Player p, double healthLevel) {
        if (healthLevel < 0 || healthLevel > 20) {
            p.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
        } else {
            p.setHealth(healthLevel);
            p.sendMessage(ChatColor.GREEN + "Your health has been set to " + healthLevel);
        }
    }

    private void setPlayerHealth(Player sender, Player target, double healthLevel, boolean all) {
        if (healthLevel < 0 || healthLevel > 20) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid number between 0 and 20!");
        } else {
            target.setHealth(healthLevel);
            target.sendMessage(ChatColor.GREEN + "Your health has been set to " + healthLevel);
            if (!all) {
                sender.sendMessage(ChatColor.GREEN + "You set health to " + healthLevel + " for " + target.getName());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Usage: /sethealth <health> [all | player | player1 player2 player3...]");
                return true;
            }

            if (args.length == 1) {
                try {
                    double healthLevel = Double.parseDouble(args[0]);
                    this.setPlayerHealth(p, healthLevel);
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Please enter a valid number!");
                }
            }

            try {
                double healthLevel = Double.parseDouble(args[0]);
                if (args[1].equalsIgnoreCase("all") && args.length == 2) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        this.setPlayerHealth(p, target, healthLevel, true);
                    }
                    p.sendMessage(ChatColor.GREEN + "You set health to " + healthLevel + " for all players on the server!");
                    return true;
                } else {
                    for (int i = 1; i < args.length; i++) {
                        String playerName = args[i];
                        try {
                            Player target = Bukkit.getServer().getPlayerExact(playerName);
                            this.setPlayerHealth(target, healthLevel);
                        } catch (Exception NullPointerException) {
                            sender.sendMessage(ChatColor.RED + playerName + ChatColor.WHITE + " is not online.");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                p.sendMessage(ChatColor.RED + "Please enter a valid number!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            completions.add("all"); // Suggest "all"
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName()); // Suggest player names
            }
        }

        return completions;
    }
}