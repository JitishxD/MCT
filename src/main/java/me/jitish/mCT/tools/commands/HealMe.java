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

public class HealMe implements CommandExecutor, TabCompleter {
    private void healPlayer(Player p) {
        p.setHealth(20);
        p.sendMessage(ChatColor.GREEN + "Your health level set to max!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.healPlayer(player);
            }

            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(ChatColor.GREEN + "You healed all players on the server!");
            }
            return true;
        }

        if (args.length > 0) {
            for (String playerName : args) {
                try {
                    Player target = Bukkit.getServer().getPlayerExact(playerName);
                    this.healPlayer(target);
                } catch (Exception NullPointerException) {
                    sender.sendMessage(ChatColor.RED + playerName + ChatColor.WHITE + " is not online.");
                }
            }
        } else if (sender instanceof Player) {
            Player p = (Player) sender;
            this.healPlayer(p);
        } else {
            sender.sendMessage(ChatColor.RED + "You must specify a player when running from the console.");
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
        }

        return completions;
    }
}
