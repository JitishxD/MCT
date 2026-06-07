package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class NightVision implements CommandExecutor, TabCompleter {
    private void toggleNightVision(Player p) {
        if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            p.sendMessage(ChatColor.RED + "Night vision disabled.");
        } else {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            p.sendMessage(ChatColor.GREEN + "Night vision enabled.");
        }
    }

    private void toggleNightVision(Player target, Player sender) {
        if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            target.removePotionEffect(PotionEffectType.NIGHT_VISION);
            target.sendMessage(ChatColor.RED + "Night vision disabled.");
            sender.sendMessage("You have disabled night vision for " + ChatColor.RED + target.getDisplayName());
        } else {
            target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            target.sendMessage(ChatColor.GREEN + "Night vision enabled.");
            sender.sendMessage("You have enabled night vision for " + ChatColor.GREEN + target.getDisplayName());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    this.toggleNightVision(player);
                }
                p.sendMessage(ChatColor.GREEN + "You toggled nightvision for all players on the server!");
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("all")) {
                if (args[1].equalsIgnoreCase("on")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                        player.sendMessage(ChatColor.GREEN + "nightvision enabled!");
                    }
                    p.sendMessage(ChatColor.GREEN + "You turned on nightvision for all players on the server!");
                } else if (args[1].equalsIgnoreCase("off")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        player.sendMessage(ChatColor.RED + "nightvision disabled!");
                    }
                    p.sendMessage(ChatColor.RED + "You turned off nightvisoin for all players on the server!");
                }
                return true;
            } else if (args.length > 0) {
                for (String playerName : args) {
                    try {
                        Player target = Bukkit.getServer().getPlayerExact(playerName);
                        this.toggleNightVision(target);
                        sender.sendMessage("You have toggled nightvision for " + ChatColor.GREEN + target.getDisplayName());
                    } catch (Exception NullPointerException) {
                        sender.sendMessage(ChatColor.RED + playerName + ChatColor.WHITE + " is not online.");
                    }
                }
            } else {
                this.toggleNightVision(p);
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

