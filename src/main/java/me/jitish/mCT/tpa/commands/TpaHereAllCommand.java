package me.jitish.mCT.tpa.commands;

import me.jitish.mCT.tpa.TpaManager;
import me.jitish.mCT.tpa.TpaSettings;
import me.jitish.mCT.tpa.TpaStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/** /tpahereall — Request ALL online players to teleport to you (admin). */
public class TpaHereAllCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;
    private final TpaSettings settings;

    public TpaHereAllCommand(TpaManager manager, TpaStorage storage, TpaSettings settings) {
        this.manager = manager;
        this.storage = storage;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("MCT.tpahereall")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        if (manager.isWorldDisabled(player)) {
            manager.sendPrefixed(player, ChatColor.RED + "TPA is disabled in this world.");
            return true;
        }

        int remaining = manager.checkCooldown(player, storage.tpaHereAllCooldowns,
                settings.getTpaHereAllCooldown(), "tpahereall");
        if (remaining > 0) {
            manager.sendPrefixed(player, ChatColor.RED + "You must wait " + remaining + " seconds before using this again.");
            return true;
        }

        int count = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(player)) continue;
            if (manager.isVanished(target)) continue;
            if (manager.isWorldDisabled(target)) continue;
            if (storage.tpaDisabled.contains(target.getUniqueId())) continue;

            manager.sendTpaHereAllRequest(player, target);
            count++;
        }

        if (count == 0) {
            manager.sendPrefixed(player, ChatColor.YELLOW + "No eligible players to send requests to.");
        } else {
            manager.sendPrefixed(player, ChatColor.GREEN + "Sent teleport-here requests to " + ChatColor.YELLOW + count + ChatColor.GREEN + " players.");
            manager.setCooldown(player, storage.tpaHereAllCooldowns);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
