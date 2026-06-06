package me.jitish.mCT.tpa;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Handles TPA-related events: death/quit location saving, move-cancel during
 * teleport delay, and optional teleport logging for /back.
 */
public class TpaListener implements Listener {

    private final TpaManager manager;
    private final TpaStorage storage;
    private final TpaSettings settings;

    public TpaListener(TpaManager manager, TpaStorage storage, TpaSettings settings) {
        this.manager = manager;
        this.storage = storage;
        this.settings = settings;
    }

    /**
     * Save death location for /back if enabled and player has permission.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!settings.isBackOnDeath()) return;
        if (!player.hasPermission("MCT.back")) return;

        Location deathLoc = player.getLocation().clone();
        storage.backLocations.put(player.getUniqueId(), deathLoc);
    }

    /**
     * On quit: save location for /back, cleanup pending requests.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Save quit location for /back
        if (settings.isBackOnQuit() && player.hasPermission("MCT.back")) {
            storage.backLocations.put(player.getUniqueId(), player.getLocation().clone());
        }

        // Reset invulnerability in case they quit during invincibility window
        player.setInvulnerable(false);

        // Cleanup pending requests (but keep toggles and back locations)
        storage.cleanupPlayer(player.getUniqueId());
    }

    /**
     * Cancel teleport if player moves more than 2 blocks during the delay countdown.
     * Uses block-level check first for performance, then distance check.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!settings.isMoveCancel()) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        // Quick block-level check to avoid expensive distance calc on every pixel
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        java.util.UUID playerId = player.getUniqueId();

        // Check pending TPA teleport
        Location stored = storage.pendingTeleportLocations.get(playerId);
        if (stored != null && stored.distanceSquared(to) > 4) { // 2 blocks squared = 4
            storage.pendingTeleportLocations.remove(playerId);
            manager.sendPrefixed(player, ChatColor.RED + "Teleport cancelled because you moved.");
        }

        // Check pending /back teleport
        Location storedBack = storage.pendingBackLocations.get(playerId);
        if (storedBack != null && storedBack.distanceSquared(to) > 4) {
            storage.pendingBackLocations.remove(playerId);
            manager.sendPrefixed(player, ChatColor.RED + "Teleport cancelled because you moved.");
        }
    }

    /**
     * Optionally log ALL teleport events for /back (from any source — other plugins, /tp, etc.)
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!settings.isTeleportLogging()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("MCT.back")) return;

        Location from = event.getFrom();
        if (from.getWorld() != null) {
            storage.backLocations.put(player.getUniqueId(), from.clone());
        }
    }
}
