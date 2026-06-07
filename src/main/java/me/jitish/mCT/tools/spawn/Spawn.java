package me.jitish.mCT.tools.spawn;

import me.jitish.mCT.MCT;
import me.jitish.mCT.tools.LocationUtil;
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
            Location location = null;
            if (this.pluginInstance.getConfig().isConfigurationSection("spawn")) {
                location = LocationUtil.loadLocation(this.pluginInstance.getConfig().getConfigurationSection("spawn"));
            } else if (this.pluginInstance.getConfig().get("spawn") instanceof Location) {
                // Legacy fallback for !!org.bukkit.Location if somehow loaded
                location = (Location) this.pluginInstance.getConfig().get("spawn");
            }
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
