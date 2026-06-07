package me.jitish.mCT.tools.listeners;

import me.jitish.mCT.MCT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkListener implements Listener {

    private final Map<UUID, ArmorStand> afkDisplays = new HashMap<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();

    private int afkTimeoutSeconds = 300; // 5 minutes
    private int idleCheckTaskId = -1;
    private int hologramTaskId = -1;

    public void startIdleChecker() {
        afkTimeoutSeconds = MCT.getPluginInstanceVar().getConfig().getInt("afk-timeout-seconds", 300);

        idleCheckTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                MCT.getPluginInstanceVar(),
                this::checkIdlePlayers,
                20L * 10,
                20L * 5
        );

        hologramTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                MCT.getPluginInstanceVar(),
                this::updateHolograms,
                1L,
                1L
        );
    }

    private void updateHolograms() {
        for (Map.Entry<UUID, ArmorStand> entry : afkDisplays.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                org.bukkit.Location loc = player.getLocation().clone().add(0, 1.9, 0);
                entry.getValue().teleport(loc);
            }
        }
    }

    public void stopIdleChecker() {
        if (idleCheckTaskId != -1) {
            Bukkit.getScheduler().cancelTask(idleCheckTaskId);
            idleCheckTaskId = -1;
        }
        if (hologramTaskId != -1) {
            Bukkit.getScheduler().cancelTask(hologramTaskId);
            hologramTaskId = -1;
        }
        for (Map.Entry<UUID, ArmorStand> entry : afkDisplays.entrySet()) {
            entry.getValue().remove();
        }
        afkDisplays.clear();
        lastActivity.clear();
    }

    private void checkIdlePlayers() {
        long now = System.currentTimeMillis();
        long timeoutMillis = afkTimeoutSeconds * 1000L;

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Long lastActive = lastActivity.get(uuid);
            if (lastActive == null) {
                continue;
            }

            if (!isAfk(player) && (now - lastActive) >= timeoutMillis) {
                enableAfk(player);
                player.sendMessage(ChatColor.GRAY + "You have been marked as AFK due to inactivity.");
            }
        }
    }

    public boolean isAfk(Player player) {
        return afkDisplays.containsKey(player.getUniqueId());
    }

    public void setAfk(Player player, boolean afk) {
        if (afk) {
            enableAfk(player);
        } else {
            disableAfk(player);
        }
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void enableAfk(Player player) {
        UUID uuid = player.getUniqueId();
        if (afkDisplays.containsKey(uuid)) {
            return;
        }

        ArmorStand display = player.getWorld().spawn(player.getLocation().add(0, 1.9, 0), ArmorStand.class);
        display.setCustomName(ChatColor.GRAY + "" + ChatColor.BOLD + "AFK");
        display.setCustomNameVisible(true);
        display.setVisible(false);
        display.setGravity(false);
        try { display.setMarker(true); } catch (Throwable ignored) {}

        afkDisplays.put(uuid, display);

        Bukkit.broadcastMessage(ChatColor.GRAY + "* " + player.getDisplayName() + " is now AFK.");
    }

    private void disableAfk(Player player) {
        UUID uuid = player.getUniqueId();
        ArmorStand display = afkDisplays.remove(uuid);
        if (display != null) {
            display.remove();
            Bukkit.broadcastMessage(ChatColor.GRAY + "* " + player.getDisplayName() + " is no longer AFK.");
        }
    }

    private void markActive(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        if (isAfk(player)) {
            disableAfk(player);
            player.sendMessage(ChatColor.GREEN + "You are no longer AFK.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        lastActivity.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            markActive(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        markActive(event.getEntity());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(MCT.getPluginInstanceVar(), () -> markActive(player));
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/afk")) {
            return;
        }
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        markActive(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        lastActivity.remove(uuid);
        ArmorStand display = afkDisplays.remove(uuid);
        if (display != null) {
            display.remove();
        }
    }
}
