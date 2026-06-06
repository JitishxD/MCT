package me.jitish.mCT.tpa.commands;

import me.jitish.mCT.tpa.TpaManager;
import me.jitish.mCT.tpa.TpaStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** /tpauto — Toggle auto-accepting all incoming /tpa requests. */
public class TpautoCommand implements CommandExecutor, TabCompleter {

    private final TpaManager manager;
    private final TpaStorage storage;

    public TpautoCommand(TpaManager manager, TpaStorage storage) {
        this.manager = manager;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("MCT.tpauto")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        if (storage.tpaAuto.contains(playerId)) {
            storage.tpaAuto.remove(playerId);
            manager.sendPrefixed(player, ChatColor.RED + "Auto-accept is now " + ChatColor.BOLD + "disabled" + ChatColor.RED + ". Requests need manual approval.");
        } else {
            storage.tpaAuto.add(playerId);
            manager.sendPrefixed(player, ChatColor.GREEN + "Auto-accept is now " + ChatColor.BOLD + "enabled" + ChatColor.GREEN + ". All /tpa requests will be accepted automatically.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
