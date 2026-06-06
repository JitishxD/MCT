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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** /back [player] — Teleport to your previous location. */
public class BackCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;
    private final TpaSettings settings;

    public BackCommand(TpaManager manager, TpaStorage storage, TpaSettings settings) {
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
        if (!settings.isBackEnabled()) {
            manager.sendPrefixed(player, ChatColor.RED + "The /back command is disabled.");
            return true;
        }
        if (!player.hasPermission("MCT.back")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // /back <player> — teleport another player to their back location
        if (args.length == 1) {
            if (!player.hasPermission("MCT.back.others")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to use /back on other players.");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                manager.sendPrefixed(player, ChatColor.RED + "Player " + args[0] + " is not online.");
                return true;
            }
            manager.teleportBackOther(player, target);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " [player]");
            return true;
        }

        // /back — teleport self to last location
        int remaining = manager.checkCooldown(player, storage.backCooldowns, settings.getBackCooldown(), "back");
        if (remaining > 0) {
            manager.sendPrefixed(player, ChatColor.RED + "You must wait " + remaining + " seconds before using /back again.");
            return true;
        }

        if (manager.teleportBack(player)) {
            manager.setCooldown(player, storage.backCooldowns);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        // Only suggest players if sender has .others permission
        if (!player.hasPermission("MCT.back.others")) {
            return Collections.emptyList();
        }
        List<String> options = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            options.add(online.getName());
        }
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], options, completions);
        Collections.sort(completions);
        return completions;
    }
}
