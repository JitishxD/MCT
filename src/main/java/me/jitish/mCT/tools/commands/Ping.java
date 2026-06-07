package me.jitish.mCT.tools.commands;

import me.jitish.mCT.MCT;
import me.jitish.mCT.tools.listeners.PingDisplayListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ping implements CommandExecutor, TabCompleter {

    private final PingDisplayListener displayListener;

    public Ping(PingDisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("MCT.ping")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        boolean turningOn;
        if (args.length == 0) {
            turningOn = displayListener.isDisabled(player);
        } else if (args[0].equalsIgnoreCase("on")) {
            turningOn = true;
        } else if (args[0].equalsIgnoreCase("off")) {
            turningOn = false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "Usage: /ping [on|off]");
            return true;
        }

        if (turningOn) {
            boolean enabled = displayListener.enable(player);
            if (!enabled) {
                player.sendMessage(ChatColor.RED + "You lack permission to enable ping display.");
                return true;
            }
            int ping = MCT.getPluginInstanceVar().versionHandler.getPlayerPing(player);
            player.sendMessage(ChatColor.GREEN + "Ping display enabled. Current ping: " + ChatColor.YELLOW + ping + ChatColor.GREEN + " ms.");
            MCT.getPluginInstanceVar().versionHandler.sendActionBar(player, ChatColor.GOLD + "Ping: " + ChatColor.YELLOW + ping + " ms");
        } else {
            displayListener.disable(player);
            player.sendMessage(ChatColor.RED + "Ping display disabled.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("on", "off");
        }
        return Collections.emptyList();
    }
}
