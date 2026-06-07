package me.jitish.mCT.tools.spawn;

import me.jitish.mCT.MCT;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnEvents implements Listener {

    private final MCT pluginInstance;

    public SpawnEvents(MCT pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        //When a player joins for the first time, teleport them to the spawn if it is set
        if (!e.getPlayer().hasPlayedBefore()) {
            Location location = (Location) pluginInstance.getConfig().get("spawn");
            if (location != null) {
                //spawn them
                e.getPlayer().teleport(location);
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerRespawnEvent e) {
        //When the player dies, respawn them at the spawn location if set
        Location location = (Location) pluginInstance.getConfig().get("spawn");
        if (location != null) {
            //spawn them
            e.setRespawnLocation(location);
        }
    }


//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        Player player = event.getPlayer();
//        Block blockPlayerLookingAt = player.getTargetBlock(null, 10);
//        if (blockPlayerLookingAt.getType() != Material.AIR) {
//            blockPlayerLookingAt.setType(Material.TNT);
//        }
//    }

}
