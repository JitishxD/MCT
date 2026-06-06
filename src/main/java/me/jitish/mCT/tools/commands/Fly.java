package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Fly implements CommandExecutor, TabCompleter {

    private void toggleFlying(Player p) {
        if (!p.getAllowFlight()) {
            p.setAllowFlight(true);
            p.setFlying(true);
            p.sendMessage(ChatColor.GREEN + "Fly mode enabled!");
        } else {
            p.setAllowFlight(false);
            p.setFlying(false);
            p.sendMessage(ChatColor.RED + "Fly mode disabled!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (!p.hasPermission("MCT.fly")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
                return true;
            }
        }

        if (sender instanceof Player p) {
            if (args.length == 1 && args[0].equalsIgnoreCase("all") && p.hasPermission("MCT.fly.toOtherPlayers")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.toggleFlying(player);
                }
                p.sendMessage(ChatColor.GREEN + "You toggled fly mode for all players on the server!");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("all") && p.hasPermission("MCT.fly.toOtherPlayers")) {
                if (args[1].equalsIgnoreCase("on")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        player.sendMessage(ChatColor.GREEN + "Fly mode enabled!");

                    }
                    p.sendMessage(ChatColor.GREEN + "You turned on fly mode for all players on the server!");
                } else if (args[1].equalsIgnoreCase("off")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.sendMessage(ChatColor.RED + "Fly mode disabled!");

                    }
                    p.sendMessage(ChatColor.RED + "You turned off fly mode for all players on the server!");
                }
                return true;
            } else if (args.length > 0 && p.hasPermission("MCT.fly.toOtherPlayers")) {
                for (String playerName : args) {
                    try {
                        Player target = Bukkit.getServer().getPlayerExact(playerName);
                        this.toggleFlying(target);
                        sender.sendMessage("You have toggled fly mode for " + ChatColor.GREEN + target.getDisplayName());
                    } catch (Exception NullPointerException) {
                        sender.sendMessage(ChatColor.RED + playerName + ChatColor.WHITE + " is not online.");
                    }
                }
            } else {
                this.toggleFlying(p);
            }
        } else {
            System.out.println("Come to the server bro");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("all"); // Suggest "all"
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName()); // Suggest player names
            }
        } else if (args.length == 2) {
            completions.add("off");
            completions.add("on");
        }

        return completions;
    }
}
