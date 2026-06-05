package me.jitish.mCT.warps;

import org.bukkit.Location;

import java.util.UUID;

public class WarpPoint {
    private final String name;
    private final UUID ownerId;
    private final String ownerName;
    private final Location location;
    private int visits;
    private final long createdAt;

    public WarpPoint(String name, UUID ownerId, String ownerName, Location location, int visits, long createdAt) {
        this.name = name;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.location = location;
        this.visits = visits;
        this.createdAt = createdAt;
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
}
