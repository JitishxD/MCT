package me.jitish.mCT.warps;

import me.jitish.mCT.MCT;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpStore {
    private final MCT plugin;
    private final String path;
    private final String label;
    private final Map<String, WarpPoint> warps = new LinkedHashMap<>();

    public WarpStore(MCT plugin, String path, String label) {
        this.plugin = plugin;
        this.path = path;
        this.label = label;
        load();
    }

    public void load() {
        warps.clear();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection warpSection = section.getConfigurationSection(key);
            if (warpSection == null) {
                continue;
            }

            String name = warpSection.getString("name", key);
            String ownerIdText = warpSection.getString("owner-id");
            Location location = (Location) warpSection.get("location");
            if (ownerIdText == null || location == null || location.getWorld() == null) {
                plugin.getLogger().warning("Skipping invalid " + label + ": " + key);
                continue;
            }

            try {
                UUID ownerId = UUID.fromString(ownerIdText);
                String ownerName = warpSection.getString("owner-name", "Unknown");
                int visits = warpSection.getInt("visits", 0);
                long createdAt = warpSection.getLong("created-at", System.currentTimeMillis());
                boolean isPrivate = warpSection.getBoolean("private", false);

                Set<UUID> allowedPlayers = new HashSet<>();
                List<String> allowedList = warpSection.getStringList("allowed-players");
                for (String uuidStr : allowedList) {
                    try {
                        allowedPlayers.add(UUID.fromString(uuidStr));
                    } catch (IllegalArgumentException ignored) {
                        plugin.getLogger().warning("Skipping invalid allowed-player UUID in " + label + " '" + key + "': " + uuidStr);
                    }
                }

                warps.put(normalize(name), new WarpPoint(name, ownerId, ownerName, location, visits, createdAt,
                        isPrivate, allowedPlayers));
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Skipping " + label + " with invalid owner UUID: " + key);
            }
        }
    }

    public void save() {
        plugin.getConfig().set(path, null);
        ConfigurationSection section = plugin.getConfig().createSection(path);

        for (WarpPoint warp : warps.values()) {
            ConfigurationSection warpSection = section.createSection(normalize(warp.getName()));
            warpSection.set("name", warp.getName());
            warpSection.set("owner-id", warp.getOwnerId().toString());
            warpSection.set("owner-name", warp.getOwnerName());
            warpSection.set("location", warp.getLocation());
            warpSection.set("visits", warp.getVisits());
            warpSection.set("created-at", warp.getCreatedAt());
            warpSection.set("private", warp.isPrivate());
            warpSection.set("allowed-players",
                    warp.getAllowedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
        }

        plugin.saveConfig();
    }

    public WarpPoint create(Player player, String name) {
        WarpPoint warp = new WarpPoint(
                name,
                player.getUniqueId(),
                player.getName(),
                player.getLocation(),
                0,
                System.currentTimeMillis(),
                false,
                new HashSet<>()
        );
        warps.put(normalize(name), warp);
        save();
        return warp;
    }

    public boolean remove(String name) {
        WarpPoint removed = warps.remove(normalize(name));
        if (removed != null) {
            save();
            return true;
        }
        return false;
    }

    public Optional<WarpPoint> get(String name) {
        return Optional.ofNullable(warps.get(normalize(name)));
    }

    public boolean exists(String name) {
        return warps.containsKey(normalize(name));
    }

    public List<WarpPoint> all() {
        List<WarpPoint> sortedWarps = new ArrayList<>(warps.values());
        sortedWarps.sort((first, second) -> first.getName().compareToIgnoreCase(second.getName()));
        return Collections.unmodifiableList(sortedWarps);
    }

    public List<String> names() {
        List<String> names = new ArrayList<>();
        for (WarpPoint warp : all()) {
            names.add(warp.getName());
        }
        return names;
    }

    /**
     * Returns warp names that the given player can access (public warps, owned warps,
     * and warps where the player has been explicitly allowed).
     * Used for tab-completion to hide private warps from unauthorized players.
     */
    public List<String> accessibleNames(UUID playerId) {
        List<String> names = new ArrayList<>();
        for (WarpPoint warp : all()) {
            if (warp.canAccess(playerId)) {
                names.add(warp.getName());
            }
        }
        return names;
    }

    /**
     * Returns warps owned by the given player name (case-insensitive match).
     */
    public List<WarpPoint> byOwner(String ownerName) {
        List<WarpPoint> result = new ArrayList<>();
        for (WarpPoint warp : all()) {
            if (warp.getOwnerName().equalsIgnoreCase(ownerName)) {
                result.add(warp);
            }
        }
        return result;
    }

    /**
     * Returns a deduplicated, sorted list of all warp owner names.
     * Used for tab-completion of the list filter.
     */
    public List<String> ownerNames() {
        Set<String> names = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (WarpPoint warp : warps.values()) {
            names.add(warp.getOwnerName());
        }
        return new ArrayList<>(names);
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ENGLISH);
    }
}
