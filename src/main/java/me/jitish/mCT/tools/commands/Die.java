package me.jitish.mCT.tools.commands;

import me.jitish.mCT.MCT;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Die implements CommandExecutor, Listener {

    private final MCT pluginInstance = MCT.getPluginInstanceVar();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("die")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.setHealth(0.0);
                //player.sendMessage(ChatColor.RED + "You have opted to die!");
                //System.out.println((player.getLastDamageCause().getCause())); // This will be null


            } else if (sender instanceof ConsoleCommandSender) {
                System.out.println("Command was run by a server console.");
            } else if (sender instanceof BlockCommandSender) {
                System.out.println("Command was run by a command block.");
            }

            return true;
        }

        return false;
    }

    /*
    //First LOWEST>LOW>NORMAL>HIGH>MONITOR are executed in this order
    //@EventHandler(priority = EventPriority.LOWEST)
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // fixme make this functional because it changes death message for all type of death.
        String customDeathMessage = player.getName() + " has opted to die!!!!";
        event.setDeathMessage(ChatColor.RED + customDeathMessage);
    }
    */
}
