package me.jitish.mCT.tpa;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * In-memory storage for all TPA state. All data is ephemeral (lost on restart).
 * TPA requests, cooldowns, back locations, and toggle states are all stored here.
 */
public class TpaStorage {

    // Request maps: receiver UUID -> sender UUID
    public final Map<UUID, UUID> tpaRequests = new HashMap<>();
    public final Map<UUID, UUID> tpaHereRequests = new HashMap<>();
    public final Map<UUID, UUID> tpaHereAllRequests = new HashMap<>();

    // Back locations: player UUID -> Location
    public final Map<UUID, Location> backLocations = new HashMap<>();

    // Movement tracking during teleport delay (for move-cancel)
    public final Map<UUID, Location> pendingTeleportLocations = new HashMap<>();
    public final Map<UUID, Location> pendingBackLocations = new HashMap<>();

    // Cooldowns: player UUID -> last use timestamp (System.currentTimeMillis)
    public final Map<UUID, Long> tpaCooldowns = new HashMap<>();
    public final Map<UUID, Long> tpaHereCooldowns = new HashMap<>();
    public final Map<UUID, Long> tpaHereAllCooldowns = new HashMap<>();
    public final Map<UUID, Long> backCooldowns = new HashMap<>();

    // Toggles
    public final Set<UUID> tpaDisabled = new HashSet<>();
    public final Set<UUID> tpaAuto = new HashSet<>();
    public final Map<UUID, Set<UUID>> ignoredPlayers = new HashMap<>();

    /** Clear all state (called on plugin disable). */
    public void clearAll() {
        tpaRequests.clear();
        tpaHereRequests.clear();
        tpaHereAllRequests.clear();
        backLocations.clear();
        pendingTeleportLocations.clear();
        pendingBackLocations.clear();
        tpaCooldowns.clear();
        tpaHereCooldowns.clear();
        tpaHereAllCooldowns.clear();
        backCooldowns.clear();
        tpaDisabled.clear();
        tpaAuto.clear();
        ignoredPlayers.clear();
    }

    /**
     * Remove all pending request/location data for a player (called on quit).
     * Keeps backLocations and toggles (tpaDisabled, tpaAuto, ignoredPlayers)
     * so they persist across reconnects within the same server session.
     */
    public void cleanupPlayer(UUID playerId) {
        // Remove as receiver from all request maps
        tpaRequests.remove(playerId);
        tpaHereRequests.remove(playerId);
        tpaHereAllRequests.remove(playerId);

        // Remove as sender from all request maps
        removeByValue(tpaRequests, playerId);
        removeByValue(tpaHereRequests, playerId);
        removeByValue(tpaHereAllRequests, playerId);

        // Clear pending movement tracking
        pendingTeleportLocations.remove(playerId);
        pendingBackLocations.remove(playerId);
    }

    private void removeByValue(Map<UUID, UUID> map, UUID value) {
        Iterator<Map.Entry<UUID, UUID>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().equals(value)) {
                it.remove();
            }
        }
    }
}
