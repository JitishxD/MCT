package me.jitish.mCT.warps;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WarpPoint {
    private final String name;
    private final UUID ownerId;
    private final String ownerName;
    private final Location location;
    private int visits;
    private final long createdAt;
    private boolean isPrivate;
    private final Set<UUID> allowedPlayers;

    public WarpPoint(String name, UUID ownerId, String ownerName, Location location, int visits, long createdAt,
                     boolean isPrivate, Set<UUID> allowedPlayers) {
        this.name = name;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.location = location;
        this.visits = visits;
        this.createdAt = createdAt;
        this.isPrivate = isPrivate;
        this.allowedPlayers = allowedPlayers != null ? new HashSet<>(allowedPlayers) : new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Location getLocation() {
        return location;
    }

    public int getVisits() {
        return visits;
    }

    public void addVisit() {
        visits++;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Set<UUID> getAllowedPlayers() {
        return Collections.unmodifiableSet(allowedPlayers);
    }

    public boolean addAllowedPlayer(UUID playerId) {
        return allowedPlayers.add(playerId);
    }

    public boolean removeAllowedPlayer(UUID playerId) {
        return allowedPlayers.remove(playerId);
    }

    /**
     * Returns true if the given player can access this warp.
     * A warp is accessible if it is public, or the player is the owner,
     * or the player has been explicitly allowed.
     */
    public boolean canAccess(UUID playerId) {
        if (!isPrivate) {
            return true;
        }
        return ownerId.equals(playerId) || allowedPlayers.contains(playerId);
    }
}
