package me.jitish.mCT.tools.commands;

import me.jitish.mCT.tools.listeners.AfkListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Afk implements CommandExecutor, TabCompleter {

    private final AfkListener afkListener;

    public Afk(AfkListener afkListener) {
        this.afkListener = afkListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }

        if (!p.hasPermission("MCT.afk")) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        if (afkListener.isAfk(p)) {
            afkListener.setAfk(p, false);
            p.sendMessage(ChatColor.GREEN + "You are no longer AFK.");
        } else {
            afkListener.setAfk(p, true);
            p.sendMessage(ChatColor.GRAY + "You are now AFK.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
