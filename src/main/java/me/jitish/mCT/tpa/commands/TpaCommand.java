package me.jitish.mCT.tpa.commands;

import me.jitish.mCT.tpa.TpaManager;
import me.jitish.mCT.tpa.TpaSettings;
import me.jitish.mCT.tpa.TpaStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** /tpa <player> — Request to teleport to another player. */
public class TpaCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;
    private final TpaSettings settings;

    public TpaCommand(TpaManager manager, TpaStorage storage, TpaSettings settings) {
        this.manager = manager;
        this.storage = storage;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("MCT.tpa")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        if (manager.isWorldDisabled(player)) {
            manager.sendPrefixed(player, ChatColor.RED + "TPA is disabled in this world.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <player>");
            return true;
        }

        // Cooldown check
        int remaining = manager.checkCooldown(player, storage.tpaCooldowns, settings.getTpaCooldown(), "tpa");
        if (remaining > 0) {
            manager.sendPrefixed(player, ChatColor.RED + "You must wait " + remaining + " seconds before sending another request.");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            manager.sendPrefixed(player, ChatColor.RED + "Player " + args[0] + " is not online.");
            return true;
        }
        if (target.equals(player)) {
            manager.sendPrefixed(player, ChatColor.RED + "You can't teleport to yourself.");
            return true;
        }
        if (manager.isVanished(target)) {
            manager.sendPrefixed(player, ChatColor.RED + "Player " + args[0] + " is not online.");
            return true;
        }
        if (manager.isWorldDisabled(target)) {
            manager.sendPrefixed(player, ChatColor.RED + "That player is in a world where TPA is disabled.");
            return true;
        }
        if (!settings.isInterdimensionalTravel() && !player.getWorld().equals(target.getWorld())) {
            manager.sendPrefixed(player, ChatColor.RED + "Cross-world teleportation is disabled.");
            return true;
        }
        if (settings.isGamemodeCheck() && (player.getGameMode().name().equals("SPECTATOR") || target.getGameMode().name().equals("SPECTATOR"))) {
            manager.sendPrefixed(player, ChatColor.RED + "You can't send TPA requests in spectator mode.");
            return true;
        }

        // Check if target has TPA disabled
        if (storage.tpaDisabled.contains(target.getUniqueId())) {
            manager.sendPrefixed(player, ChatColor.RED + target.getName() + " has TPA requests disabled.");
            return true;
        }

        // Check if target is ignoring sender
        java.util.Set<java.util.UUID> ignored = storage.ignoredPlayers.get(target.getUniqueId());
        if (ignored != null && ignored.contains(player.getUniqueId())) {
            manager.sendPrefixed(player, ChatColor.RED + target.getName() + " is ignoring your requests.");
            return true;
        }



        if (manager.sendTpaRequest(player, target)) {
            manager.setCooldown(player, storage.tpaCooldowns);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;
        List<String> options = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(player) && !manager.isVanished(online) && player.canSee(online)) {
                options.add(online.getName());
            }
        }
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], options, completions);
        Collections.sort(completions);
        return completions;
    }
}
