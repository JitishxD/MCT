package me.jitish.mCT.tools.spawn;

import me.jitish.mCT.MCT;
import me.jitish.mCT.tools.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    private final MCT pluginInstance;

    public SetSpawn(MCT pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("MCT.setSpawn")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
                return true;
            }
        }

        //Make sure that they are a player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            //get the players location
            Location location = player.getLocation();

            //set the spawn location in the config.yml
//            pluginInstance.getConfig().set("spawn.x", location.getX());
//            pluginInstance.getConfig().set("spawn.y", location.getY());
//            pluginInstance.getConfig().set("spawn.z", location.getZ());
//            pluginInstance.getConfig().set("spawn.worldName", location.getWorld().getName());

            //set the spawn location in the config.yml
            org.bukkit.configuration.ConfigurationSection spawnSection = pluginInstance.getConfig().createSection("spawn");
            LocationUtil.saveLocation(spawnSection, location);

            //save the config.yml
            pluginInstance.saveConfig();

            //send a message to the player
            player.sendMessage("Spawn location set!");

        } else {
            System.out.println("Bruh get yo ass on the server.");
        }


        return true;
    }
}
