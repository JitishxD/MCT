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
import java.util.List;
import java.util.UUID;

/** /tpatoggle [player] — Toggle receiving TPA requests on/off. */
public class TpaToggleCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;

    public TpaToggleCommand(TpaManager manager, TpaStorage storage) {
        this.manager = manager;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("MCT.tpatoggle")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // Toggle another player
        if (args.length == 1) {
            if (!player.hasPermission("MCT.tpatoggle.others")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to toggle TPA for other players.");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null || !target.isOnline()) {
                manager.sendPrefixed(player, ChatColor.RED + "Player " + args[0] + " is not online.");
                return true;
            }
            UUID targetId = target.getUniqueId();
            if (storage.tpaDisabled.contains(targetId)) {
                storage.tpaDisabled.remove(targetId);
                manager.sendPrefixed(player, ChatColor.GREEN + "TPA requests enabled for " + target.getName() + ".");
                manager.sendPrefixed(target, ChatColor.GREEN + "Your TPA requests have been enabled by " + player.getName() + ".");
            } else {
                storage.tpaDisabled.add(targetId);
                manager.sendPrefixed(player, ChatColor.RED + "TPA requests disabled for " + target.getName() + ".");
                manager.sendPrefixed(target, ChatColor.RED + "Your TPA requests have been disabled by " + player.getName() + ".");
            }
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " [player]");
            return true;
        }

        // Toggle self
        UUID playerId = player.getUniqueId();
        if (storage.tpaDisabled.contains(playerId)) {
            storage.tpaDisabled.remove(playerId);
            manager.sendPrefixed(player, ChatColor.GREEN + "TPA requests are now " + ChatColor.BOLD + "enabled" + ChatColor.GREEN + ". Players can send you requests.");
        } else {
            storage.tpaDisabled.add(playerId);
            manager.sendPrefixed(player, ChatColor.RED + "TPA requests are now " + ChatColor.BOLD + "disabled" + ChatColor.RED + ". You won't receive requests.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }
        Player player = (Player) sender;
        if (!player.hasPermission("MCT.tpatoggle.others")) {
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
