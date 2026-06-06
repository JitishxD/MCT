package me.jitish.mCT.tpa.commands;

import me.jitish.mCT.tpa.TpaManager;
import me.jitish.mCT.tpa.TpaStorage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** /tpaignore <player> — Block/unblock a specific player's TPA requests (toggle). */
public class TpaIgnoreCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;

    public TpaIgnoreCommand(TpaManager manager, TpaStorage storage) {
        this.manager = manager;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("MCT.tpaignore")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            manager.sendPrefixed(player, ChatColor.RED + "Player " + args[0] + " is not online.");
            return true;
        }
        if (target.equals(player)) {
            manager.sendPrefixed(player, ChatColor.RED + "You can't ignore yourself.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        UUID targetId = target.getUniqueId();

        Set<UUID> ignored = storage.ignoredPlayers.computeIfAbsent(playerId, k -> new HashSet<>());

        if (ignored.contains(targetId)) {
            // Unblock
            ignored.remove(targetId);
            if (ignored.isEmpty()) {
                storage.ignoredPlayers.remove(playerId);
            }
            manager.sendPrefixed(player, ChatColor.GREEN + "You are no longer ignoring " + target.getName() + "'s TPA requests.");
        } else {
            // Block
            ignored.add(targetId);
            manager.sendPrefixed(player, ChatColor.RED + "You are now ignoring " + target.getName() + "'s TPA requests.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        List<String> options = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(player)) {
                options.add(online.getName());
            }
        }
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], options, completions);
        Collections.sort(completions);
        return completions;
    }
}
