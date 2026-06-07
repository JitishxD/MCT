package me.jitish.mCT.tpa.commands;

import me.jitish.mCT.tpa.TpaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/** /tpdeny — Deny a pending TPA request. */
public class TpDenyCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;

    public TpDenyCommand(TpaManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("MCT.tpdeny")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (!manager.denyRequest(player)) {
            manager.sendPrefixed(player, ChatColor.RED + "You have no pending teleport requests.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
