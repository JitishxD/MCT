package me.jitish.mCT.tpa;

// Modern chat imports removed for 1.7 compatibility
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.jitish.mCT.MCT;
import me.jitish.mCT.tools.compatibility.VersionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Core TPA logic: sending/accepting/denying requests, delayed teleportation,
 * safe teleport, cooldown management, and clickable chat buttons.
 */
public class TpaManager {

    /** Types of TPA requests. */
    public enum RequestType {
        TPA, TPA_HERE, TPA_HERE_ALL
    }

    private static final Set<Material> DANGEROUS_BLOCKS = new java.util.HashSet<>();
    
    static {
        try { DANGEROUS_BLOCKS.add(Material.valueOf("LAVA")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("STATIONARY_LAVA")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("FIRE")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("CACTUS")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("MAGMA_BLOCK")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("SWEET_BERRY_BUSH")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("CAMPFIRE")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("SOUL_CAMPFIRE")); } catch (Throwable t) {}
        try { DANGEROUS_BLOCKS.add(Material.valueOf("POWDER_SNOW")); } catch (Throwable t) {}
    }

    private final JavaPlugin plugin;
    private final TpaStorage storage;
    private final TpaSettings settings;

    public TpaManager(JavaPlugin plugin, TpaStorage storage, TpaSettings settings) {
        this.plugin = plugin;
        this.storage = storage;
        this.settings = settings;
    }

    // ── Validation helpers ───────────────────────────────────────

    /** Check if a player is vanished (via metadata set by vanish plugins). */
    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }

    /** Check if a player's world is disabled for TPA. */
    public boolean isWorldDisabled(Player player) {
        return settings.isWorldDisabled(player.getWorld().getName());
    }

    /**
     * Check cooldown for a player. Returns remaining seconds if on cooldown, 0 if ready.
     * Supports MCT.tpa.bypasscooldown and dynamic MCT.tpa.cooldown.<type>.<seconds> permissions.
     */
    public int checkCooldown(Player player, Map<UUID, Long> cooldownMap, int defaultCooldown, String type) {
        if (player.hasPermission("MCT.tpa.bypasscooldown")) {
            return 0;
        }

        // Check for dynamic per-player cooldown permission: MCT.tpa.cooldown.<type>.<seconds>
        int cooldown = defaultCooldown;
        String prefix = "MCT.tpa.cooldown." + type + ".";
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            String name = perm.getPermission();
            if (perm.getValue() && name.startsWith(prefix)) {
                try {
                    int custom = Integer.parseInt(name.substring(prefix.length()));
                    cooldown = custom;
                    break;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (cooldown <= 0) {
            return 0;
        }

        Long lastUse = cooldownMap.get(player.getUniqueId());
        if (lastUse == null) {
            return 0;
        }

        long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
        if (elapsed >= cooldown) {
            return 0;
        }
        return (int) (cooldown - elapsed);
    }

    /** Record current timestamp as cooldown start. */
    public void setCooldown(Player player, Map<UUID, Long> cooldownMap) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    // ── Request sending ──────────────────────────────────────────

    /**
     * Send a /tpa request (sender wants to teleport TO receiver).
     * Returns true if sent successfully, false if a duplicate request exists.
     */
    public boolean sendTpaRequest(Player sender, Player receiver) {
        if (storage.tpaAuto.contains(receiver.getUniqueId())) {
            sendPrefixed(sender, ChatColor.GREEN + receiver.getName() + " has auto-accept enabled. Teleporting...");
            sendPrefixed(receiver, ChatColor.GREEN + sender.getName() + "'s request was auto-accepted.");
            storage.tpaRequests.put(receiver.getUniqueId(), sender.getUniqueId());
            acceptRequest(receiver);
            return true;
        }

        // Check for existing request from this sender to this receiver
        UUID existing = storage.tpaRequests.get(receiver.getUniqueId());
        if (existing != null && existing.equals(sender.getUniqueId())) {
            sendPrefixed(sender, ChatColor.RED + "You already have a pending request to " + receiver.getName() + ".");
            return false;
        }

        storage.tpaRequests.put(receiver.getUniqueId(), sender.getUniqueId());
        sendRequestMessages(sender, receiver, "tpa");
        scheduleTimeout(receiver.getUniqueId(), RequestType.TPA);
        return true;
    }

    /**
     * Send a /tpahere request (sender wants receiver to teleport TO sender).
     */
    public boolean sendTpaHereRequest(Player sender, Player receiver) {
        if (storage.tpaAuto.contains(receiver.getUniqueId())) {
            sendPrefixed(sender, ChatColor.GREEN + receiver.getName() + " has auto-accept enabled. Teleporting...");
            sendPrefixed(receiver, ChatColor.GREEN + sender.getName() + "'s request was auto-accepted.");
            storage.tpaHereRequests.put(receiver.getUniqueId(), sender.getUniqueId());
            acceptRequest(receiver);
            return true;
        }

        UUID existing = storage.tpaHereRequests.get(receiver.getUniqueId());
        if (existing != null && existing.equals(sender.getUniqueId())) {
            sendPrefixed(sender, ChatColor.RED + "You already have a pending request to " + receiver.getName() + ".");
            return false;
        }

        storage.tpaHereRequests.put(receiver.getUniqueId(), sender.getUniqueId());
        sendRequestMessages(sender, receiver, "tpahere");
        scheduleTimeout(receiver.getUniqueId(), RequestType.TPA_HERE);
        return true;
    }

    /**
     * Send a /tpahereall request to a single player.
     */
    public boolean sendTpaHereAllRequest(Player sender, Player receiver) {
        if (storage.tpaAuto.contains(receiver.getUniqueId())) {
            sendPrefixed(sender, ChatColor.GREEN + receiver.getName() + " has auto-accept enabled. Teleporting...");
            sendPrefixed(receiver, ChatColor.GREEN + sender.getName() + "'s request was auto-accepted.");
            storage.tpaHereAllRequests.put(receiver.getUniqueId(), sender.getUniqueId());
            acceptRequest(receiver);
            return true;
        }

        storage.tpaHereAllRequests.put(receiver.getUniqueId(), sender.getUniqueId());
        sendRequestMessages(sender, receiver, "tpahere");
        scheduleTimeout(receiver.getUniqueId(), RequestType.TPA_HERE_ALL);
        return true;
    }

    private void sendRequestMessages(Player sender, Player receiver, String type) {
        // Notify sender
        String sentMsg = settings.formatMessage("request-sent", receiver);
        if (sentMsg == null) {
            sentMsg = ChatColor.GRAY + "Teleport request sent to " + ChatColor.YELLOW + receiver.getName() + ChatColor.GRAY + ".";
        }
        sendPrefixed(sender, sentMsg);

        // Notify receiver
        String receivedMsg = settings.formatMessage("request-received", sender);
        if (receivedMsg == null) {
            String typeLabel = type.equals("tpa") ? "teleport to you" : "you to teleport to them";
            receivedMsg = ChatColor.YELLOW + sender.getName() + ChatColor.GRAY + " wants to " + typeLabel + ".";
        }

        // Play sound
        if (settings.isSoundsEnabled()) {
            VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
            if (vh != null) {
                vh.sendSound(receiver, settings.getSound(), settings.getSoundVolume(), settings.getSoundPitch());
            }
        }

        sendPrefixed(receiver, receivedMsg);

        // Time remaining hint
        int timeout = settings.getRequestTimeout();
        sendPrefixed(receiver, ChatColor.GRAY + "This request expires in " + ChatColor.YELLOW + timeout + ChatColor.GRAY + " seconds.");

        // Clickable accept/deny buttons
        if (settings.isClickableButtons()) {
            sendClickableButtons(receiver);
        } else {
            sendPrefixed(receiver, ChatColor.GRAY + "Type " + ChatColor.GREEN + "/tpaccept" + ChatColor.GRAY + " or " + ChatColor.RED + "/tpdeny");
        }
    }

    // ── Clickable buttons ────────────────────────────────────────

    private void sendClickableButtons(Player receiver) {
        VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
        if (vh != null) {
            vh.sendTpaRequestButtons(receiver, "");
        } else {
            sendPrefixed(receiver, ChatColor.GRAY + "Type " + ChatColor.GREEN + "/tpaccept" + ChatColor.GRAY + " or " + ChatColor.RED + "/tpdeny");
        }
    }

    // ── Request accepting ────────────────────────────────────────

    /**
     * Accept a pending request. Checks tpaRequests, tpaHereRequests, tpaHereAllRequests in order.
     * Returns true if a request was found and accepted, false if no pending requests.
     */
    public boolean acceptRequest(Player acceptor) {
        UUID acceptorId = acceptor.getUniqueId();

        // Check /tpa request first: sender teleports TO acceptor
        UUID senderId = storage.tpaRequests.get(acceptorId);
        if (senderId != null) {
            storage.tpaRequests.remove(acceptorId);
            Player sender = Bukkit.getPlayer(senderId);
            if (sender == null || !sender.isOnline()) {
                sendPrefixed(acceptor, ChatColor.RED + "The requesting player is no longer online.");
                return true;
            }
            sendPrefixed(acceptor, ChatColor.GREEN + "You accepted " + sender.getName() + "'s teleport request.");
            sendPrefixed(sender, ChatColor.GREEN + acceptor.getName() + " accepted your teleport request.");
            executeTeleport(sender, acceptor.getLocation(), true);
            return true;
        }

        // Check /tpahere request: acceptor teleports TO sender
        senderId = storage.tpaHereRequests.get(acceptorId);
        if (senderId != null) {
            storage.tpaHereRequests.remove(acceptorId);
            Player sender = Bukkit.getPlayer(senderId);
            if (sender == null || !sender.isOnline()) {
                sendPrefixed(acceptor, ChatColor.RED + "The requesting player is no longer online.");
                return true;
            }
            sendPrefixed(acceptor, ChatColor.GREEN + "You accepted " + sender.getName() + "'s teleport-here request.");
            sendPrefixed(sender, ChatColor.GREEN + acceptor.getName() + " accepted your teleport-here request.");
            executeTeleport(acceptor, sender.getLocation(), true);
            return true;
        }

        // Check /tpahereall request: acceptor teleports TO sender
        senderId = storage.tpaHereAllRequests.get(acceptorId);
        if (senderId != null) {
            storage.tpaHereAllRequests.remove(acceptorId);
            Player sender = Bukkit.getPlayer(senderId);
            if (sender == null || !sender.isOnline()) {
                sendPrefixed(acceptor, ChatColor.RED + "The requesting player is no longer online.");
                return true;
            }
            sendPrefixed(acceptor, ChatColor.GREEN + "You accepted the teleport-here-all request.");
            executeTeleport(acceptor, sender.getLocation(), true);
            return true;
        }

        return false;
    }

    // ── Request denying ──────────────────────────────────────────

    /**
     * Deny all pending requests for this player. Returns true if any were found.
     */
    public boolean denyRequest(Player denier) {
        UUID denierId = denier.getUniqueId();
        boolean found = false;

        UUID senderId = storage.tpaRequests.remove(denierId);
        if (senderId != null) {
            found = true;
            Player sender = Bukkit.getPlayer(senderId);
            sendPrefixed(denier, ChatColor.RED + "You denied the teleport request.");
            if (sender != null && sender.isOnline()) {
                sendPrefixed(sender, ChatColor.RED + denier.getName() + " denied your teleport request.");
            }
        }

        senderId = storage.tpaHereRequests.remove(denierId);
        if (senderId != null) {
            found = true;
            Player sender = Bukkit.getPlayer(senderId);
            sendPrefixed(denier, ChatColor.RED + "You denied the teleport-here request.");
            if (sender != null && sender.isOnline()) {
                sendPrefixed(sender, ChatColor.RED + denier.getName() + " denied your teleport-here request.");
            }
        }

        senderId = storage.tpaHereAllRequests.remove(denierId);
        if (senderId != null) {
            found = true;
            sendPrefixed(denier, ChatColor.RED + "You denied the teleport-here-all request.");
        }

        return found;
    }

    // ── Request cancelling ───────────────────────────────────────

    /**
     * Cancel an outgoing request. The canceller must be the original sender.
     * Returns true if a request was found and cancelled.
     */
    public boolean cancelRequest(Player canceller, String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sendPrefixed(canceller, ChatColor.RED + "Player " + targetName + " is not online.");
            return false;
        }

        UUID cancellerId = canceller.getUniqueId();
        UUID targetId = target.getUniqueId();
        boolean found = false;

        UUID storedSender = storage.tpaRequests.get(targetId);
        if (storedSender != null && storedSender.equals(cancellerId)) {
            storage.tpaRequests.remove(targetId);
            storage.tpaCooldowns.remove(cancellerId);
            found = true;
        }

        storedSender = storage.tpaHereRequests.get(targetId);
        if (storedSender != null && storedSender.equals(cancellerId)) {
            storage.tpaHereRequests.remove(targetId);
            storage.tpaHereCooldowns.remove(cancellerId);
            found = true;
        }

        if (found) {
            sendPrefixed(canceller, ChatColor.YELLOW + "Teleport request to " + target.getName() + " cancelled.");
            sendPrefixed(target, ChatColor.YELLOW + canceller.getName() + " cancelled their teleport request.");
        } else {
            sendPrefixed(canceller, ChatColor.RED + "You don't have a pending request to " + target.getName() + ".");
        }

        return found;
    }

    // ── Request timeout ──────────────────────────────────────────

    private void scheduleTimeout(UUID receiverId, RequestType type) {
        int timeoutTicks = settings.getRequestTimeout() * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<UUID, UUID> map = getRequestMap(type);
                UUID senderId = map.remove(receiverId);
                if (senderId == null) {
                    return; // already accepted/denied/cancelled
                }
                Player sender = Bukkit.getPlayer(senderId);
                Player receiver = Bukkit.getPlayer(receiverId);
                if (sender != null && sender.isOnline()) {
                    sendPrefixed(sender, ChatColor.RED + "Your teleport request has timed out.");
                }
                if (receiver != null && receiver.isOnline()) {
                    sendPrefixed(receiver, ChatColor.GRAY + "A teleport request has expired.");
                }
            }
        }.runTaskLater(plugin, timeoutTicks);
    }

    private Map<UUID, UUID> getRequestMap(RequestType type) {
        switch (type) {
            case TPA: return storage.tpaRequests;
            case TPA_HERE: return storage.tpaHereRequests;
            case TPA_HERE_ALL: return storage.tpaHereAllRequests;
            default: return null;
        }
    }

    // ── Delayed teleport ─────────────────────────────────────────

    /**
     * Execute a delayed teleport. Stores the player's location for move-cancel,
     * then teleports after the configured delay.
     */
    private void executeTeleport(Player teleporting, Location destination, boolean saveBack) {
        int delay = settings.getTeleportDelay();

        if (delay <= 0) {
            performTeleport(teleporting, destination, saveBack);
            return;
        }

        // Store location for move-cancel
        storage.pendingTeleportLocations.put(teleporting.getUniqueId(), teleporting.getLocation().clone());
        sendPrefixed(teleporting, ChatColor.GRAY + "Teleporting in " + ChatColor.YELLOW + delay + ChatColor.GRAY + " seconds. Don't move!");

        new BukkitRunnable() {
            @Override
            public void run() {
                // Check if teleport was cancelled (by movement or disconnect)
                if (!storage.pendingTeleportLocations.containsKey(teleporting.getUniqueId())) {
                    return;
                }
                storage.pendingTeleportLocations.remove(teleporting.getUniqueId());

                if (!teleporting.isOnline()) {
                    return;
                }

                performTeleport(teleporting, destination, saveBack);
            }
        }.runTaskLater(plugin, delay * 20L);
    }

    private void performTeleport(Player teleporting, Location destination, boolean saveBack) {
        // Save pre-teleport location for /back
        if (saveBack) {
            storage.backLocations.put(teleporting.getUniqueId(), teleporting.getLocation().clone());
        }

        // Spawn particles
        if (settings.isParticlesEnabled()) {
            spawnParticles(teleporting);
        }

        // Teleport
        teleporting.teleport(destination);

        // Spawn particles at destination
        if (settings.isParticlesEnabled()) {
            spawnParticles(teleporting);
        }

        // Play sound
        if (settings.isSoundsEnabled()) {
            teleporting.playSound(teleporting.getLocation(), settings.getSound(),
                    settings.getSoundVolume(), settings.getSoundPitch());
        }

        // Set invincibility
        if (settings.isInvincibilityEnabled()) {
            VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
            if (vh != null) vh.setInvulnerable(teleporting, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (teleporting.isOnline()) {
                        VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
                        if (vh != null) vh.setInvulnerable(teleporting, false);
                    }
                }
            }.runTaskLater(plugin, settings.getInvincibilityDuration() * 20L);
        }

        sendPrefixed(teleporting, ChatColor.GREEN + "Teleported!");
    }
    private void spawnParticles(Player player) {
        VersionHandler vh = MCT.getPluginInstanceVar().versionHandler;
        if (vh != null) {
            vh.spawnParticle(player, settings.getParticle());
        }
    }

    // ── Back command ─────────────────────────────────────────────

    /**
     * Teleport a player to their last saved location (/back).
     * Returns true if a back location was found.
     */
    public boolean teleportBack(Player player) {
        Location backLoc = storage.backLocations.get(player.getUniqueId());
        if (backLoc == null || backLoc.getWorld() == null) {
            sendPrefixed(player, ChatColor.RED + "You have no location to go back to.");
            return false;
        }

        int delay = settings.getBackDelay();

        // Save current location as the new back point (so /back toggles)
        storage.backLocations.put(player.getUniqueId(), player.getLocation().clone());

        Location target = settings.isSafeTeleport() ? findSafeLocation(backLoc) : backLoc;

        if (delay <= 0) {
            player.teleport(target);
            if (settings.isParticlesEnabled()) {
                spawnParticles(player);
            }
            sendPrefixed(player, ChatColor.GREEN + "Teleported to your previous location.");
            return true;
        }

        // Delayed back with move-cancel
        storage.pendingBackLocations.put(player.getUniqueId(), player.getLocation().clone());
        sendPrefixed(player, ChatColor.GRAY + "Teleporting in " + ChatColor.YELLOW + delay + ChatColor.GRAY + " seconds. Don't move!");

        Location finalTarget = target;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!storage.pendingBackLocations.containsKey(player.getUniqueId())) {
                    return;
                }
                storage.pendingBackLocations.remove(player.getUniqueId());

                if (!player.isOnline()) {
                    return;
                }

                player.teleport(finalTarget);
                if (settings.isParticlesEnabled()) {
                    spawnParticles(player);
                }
                if (settings.isSoundsEnabled()) {
                    player.playSound(player.getLocation(), settings.getSound(),
                            settings.getSoundVolume(), settings.getSoundPitch());
                }
                sendPrefixed(player, ChatColor.GREEN + "Teleported to your previous location.");
            }
        }.runTaskLater(plugin, delay * 20L);

        return true;
    }

    /**
     * Teleport another player to their back location (admin, no delay).
     */
    public boolean teleportBackOther(Player executor, Player target) {
        Location backLoc = storage.backLocations.get(target.getUniqueId());
        if (backLoc == null || backLoc.getWorld() == null) {
            sendPrefixed(executor, ChatColor.RED + target.getName() + " has no location to go back to.");
            return false;
        }

        Location safeLoc = settings.isSafeTeleport() ? findSafeLocation(backLoc) : backLoc;
        target.teleport(safeLoc);
        sendPrefixed(executor, ChatColor.GREEN + "Teleported " + target.getName() + " to their previous location.");
        sendPrefixed(target, ChatColor.GREEN + "You were teleported to your previous location.");
        return true;
    }

    // ── Safe teleport ────────────────────────────────────────────

    /**
     * Find a safe location near the target. Searches in expanding radius.
     * Falls back to the original location if no safe spot found.
     */
    public Location findSafeLocation(Location target) {
        if (isSafeLocation(target)) {
            return target;
        }

        int radius = settings.getSafeTeleportRadius();
        int vertCap = settings.getSafeTeleportVerticalCap();
        World world = target.getWorld();
        if (world == null) {
            return target;
        }

        int baseX = target.getBlockX();
        int baseY = target.getBlockY();
        int baseZ = target.getBlockZ();
        int minY = MCT.getPluginInstanceVar().versionHandler.getMinHeight(world);
        int maxY = world.getMaxHeight();

        // Check directly above/below first
        for (int dy = 1; dy <= vertCap; dy++) {
            Location up = new Location(world, baseX + 0.5, baseY + dy, baseZ + 0.5, target.getYaw(), target.getPitch());
            if (up.getBlockY() < maxY && isSafeLocation(up)) {
                return up;
            }
            Location down = new Location(world, baseX + 0.5, baseY - dy, baseZ + 0.5, target.getYaw(), target.getPitch());
            if (down.getBlockY() >= minY && isSafeLocation(down)) {
                return down;
            }
        }

        // Spiral outward search
        for (int r = 1; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue; // only check perimeter
                    for (int dy = -vertCap; dy <= vertCap; dy++) {
                        int y = baseY + dy;
                        if (y < minY || y >= maxY) continue;
                        Location check = new Location(world, baseX + dx + 0.5, y, baseZ + dz + 0.5,
                                target.getYaw(), target.getPitch());
                        if (isSafeLocation(check)) {
                            return check;
                        }
                    }
                }
            }
        }

        // No safe spot found, return original with warning
        return target;
    }

    /**
     * Check if a location is safe to teleport to:
     * - Solid ground below feet
     * - Non-solid at feet and head level
     * - No dangerous blocks nearby
     */
    public boolean isSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return false;

        Block ground = world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        Block feet = world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        Block head = world.getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());

        // Ground must be solid (or water for swimming)
        if (!ground.getType().isSolid() && ground.getType() != Material.WATER) {
            return false;
        }

        // Feet and head must not be solid
        if (feet.getType().isSolid() || head.getType().isSolid()) {
            return false;
        }

        // No dangerous blocks at feet or head
        if (DANGEROUS_BLOCKS.contains(feet.getType()) || DANGEROUS_BLOCKS.contains(head.getType())) {
            return false;
        }
        if (DANGEROUS_BLOCKS.contains(ground.getType())) {
            return false;
        }

        return true;
    }

    // ── Messaging ────────────────────────────────────────────────

    /** Send a message with the configured prefix. */
    public void sendPrefixed(Player player, String message) {
        player.sendMessage(settings.getPrefix() + message);
    }
}
