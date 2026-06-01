package me.jitish.mCT.listeners;

import me.jitish.mCT.MCT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages AFK state for players.
 * When a player goes AFK (manually or after being idle), a TextDisplay hologram
 * reading "⏸ AFK" is spawned and mounted as a passenger on the player so it
 * floats above their head.
 * Moving, chatting, interacting, or running a command resets the idle timer
 * and automatically takes the player out of AFK.
 */
public class AfkListener implements Listener {

    private final Map<UUID, TextDisplay> afkDisplays = new HashMap<>();
    private final Map<UUID, Long> lastActivity = new HashMap<>();

    // Default idle timeout in seconds (can be overridden by config.yml "afk-timeout-seconds")
    private int afkTimeoutSeconds = 300; // 5 minutes
    private int idleCheckTaskId = -1;

    // yStarts the idle-check scheduler. Should be called from onEnable().
    public void startIdleChecker() {
        // Read timeout from config (fallback to 300 seconds if not set)
        afkTimeoutSeconds = MCT.getPluginInstanceVar().getConfig().getInt("afk-timeout-seconds", 300);

        idleCheckTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                MCT.getPluginInstanceVar(),
                this::checkIdlePlayers,
                20L * 10, // First check after 10 seconds
                20L * 5   // Check every 5 seconds
        );
    }

    // Stops the idle-check scheduler. Should be called from onDisable().
    public void stopIdleChecker() {
        if (idleCheckTaskId != -1) {
            Bukkit.getScheduler().cancelTask(idleCheckTaskId);
            idleCheckTaskId = -1;
        }
        // Cleanup all AFK displays
        for (Map.Entry<UUID, TextDisplay> entry : afkDisplays.entrySet()) {
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

    // Check whether a player is currently marked AFK.
    public boolean isAfk(Player player) {
        return afkDisplays.containsKey(player.getUniqueId());
    }

    // Toggle or set a player's AFK state.
    public void setAfk(Player player, boolean afk) {
        if (afk) {
            enableAfk(player);
        } else {
            disableAfk(player);
        }
        // Reset activity timer regardless
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void enableAfk(Player player) {
        UUID uuid = player.getUniqueId();
        if (afkDisplays.containsKey(uuid)) {
            return; // already AFK
        }

        // Spawn a TextDisplay entity at the player's location
        TextDisplay display = player.getWorld().spawn(player.getLocation(), TextDisplay.class, entity -> {
            entity.setText(ChatColor.GRAY + "" + ChatColor.BOLD + "⏸ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "AFK");
            entity.setBillboard(Display.Billboard.CENTER); // Always face the viewer
            entity.setDefaultBackground(false); // Transparent background
            entity.setShadowed(true);
            entity.setPersistent(false); // Don't save to world data
        });

        // Mount the TextDisplay on top of the player so it floats above their head
        player.addPassenger(display);
        afkDisplays.put(uuid, display);

        // Broadcast to the server
        Bukkit.broadcastMessage(ChatColor.GRAY + "* " + player.getDisplayName() + " is now AFK.");
    }

    private void disableAfk(Player player) {
        UUID uuid = player.getUniqueId();
        TextDisplay display = afkDisplays.remove(uuid);
        if (display != null) {
            player.removePassenger(display);
            display.remove();
            Bukkit.broadcastMessage(ChatColor.GRAY + "* " + player.getDisplayName() + " is no longer AFK.");
        }
    }

    /**
     * Resets the idle timer for a player and un-AFKs them if they are AFK.
     */
    private void markActive(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        if (isAfk(player)) {
            disableAfk(player);
            player.sendMessage(ChatColor.GREEN + "You are no longer AFK.");
        }
    }

    // --- Activity listeners (reset idle timer + auto-unafk) ---

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Start tracking activity from the moment they join
        lastActivity.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only trigger on actual position change, not just head rotation
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            markActive(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Schedule on main thread since we need to interact with entities
        Bukkit.getScheduler().runTask(MCT.getPluginInstanceVar(), () -> markActive(player));
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        // Don't reset if the player is running /afk (toggle scenario handled by command)
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
        // Cleanup on disconnect
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        lastActivity.remove(uuid);
        TextDisplay display = afkDisplays.remove(uuid);
        if (display != null) {
            display.remove();
        }
    }
}
