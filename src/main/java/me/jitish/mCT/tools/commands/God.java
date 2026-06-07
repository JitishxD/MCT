package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.jitish.mCT.MCT;
import me.jitish.mCT.tools.compatibility.VersionHandler;

import java.util.ArrayList;
import java.util.List;

public class God implements CommandExecutor, TabCompleter {

    private void toggleGodMode(Player p) {
        VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
        if (vh != null && vh.isInvulnerable(p)) {
            if (vh != null) vh.setInvulnerable(p, false);
            p.sendMessage(ChatColor.RED + "God mode is disabled!");
        } else {
            if (vh != null) vh.setInvulnerable(p, true);
            p.sendMessage(ChatColor.GREEN + "God mode is enabled!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("MCT.godMode")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
                return true;
            }
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("all") && p.hasPermission("MCT.godMode.toOtherPlayers")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.toggleGodMode(player);
                }
                p.sendMessage(ChatColor.GREEN + "You toggled God mode for all players on the server!");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("all") && p.hasPermission("MCT.godMode.toOtherPlayers")) {
                if (args[1].equalsIgnoreCase("on")) {
                    VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (vh != null) vh.setInvulnerable(player, true);
                        player.sendMessage(ChatColor.GREEN + "God mode enabled!");
                    }
                    p.sendMessage(ChatColor.GREEN + "You turned on God mode for all players on the server!");
                } else if (args[1].equalsIgnoreCase("off")) {
                    VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (vh != null) vh.setInvulnerable(player, false);
                        player.sendMessage(ChatColor.RED + "God mode disabled!");
                    }
                    p.sendMessage(ChatColor.RED + "You turned off god mode for all players on the server!");
                }
                return true;
            } else if (args.length > 0 && p.hasPermission("MCT.godMode.toOtherPlayers")) {
                for (String playerName : args) {
                    try {
                        Player target = Bukkit.getServer().getPlayerExact(playerName);
                        this.toggleGodMode(target);
                        sender.sendMessage("You have toggled Godmode for " + ChatColor.GREEN + target.getDisplayName());
                    } catch (Exception NullPointerException) {
                        sender.sendMessage(ChatColor.RED + playerName + ChatColor.WHITE + " is not online.");
                    }
                }
            } else {
                this.toggleGodMode(p);
            }
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
        } else if (args.length == 2) {
            completions.add("off");
            completions.add("on");
        }

        return completions;
    }
}
