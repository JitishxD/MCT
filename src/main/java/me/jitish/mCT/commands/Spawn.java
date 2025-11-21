package me.jitish.mCT.commands;

import me.jitish.mCT.MCT;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {
    private final MCT pluginInstance;

    public Spawn(MCT pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = this.pluginInstance.getConfig().getLocation("spawn");
            if (location != null) {
                player.teleport(location);
                player.sendMessage("You have been teleported to the spawn point.");
            } else {
                player.sendMessage("There is no spawn point set.");
            }
        } else {
            System.out.println("Bruh get yo ass on the server.");
        }

        return true;
    }
}
