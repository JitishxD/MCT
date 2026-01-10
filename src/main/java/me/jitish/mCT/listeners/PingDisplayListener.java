package me.jitish.mCT.listeners;

import me.jitish.mCT.MCT;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps each eligible player's ping visible in their action bar while online.
 */
public class PingDisplayListener implements Listener {

    private final Map<UUID, Integer> taskIds = new HashMap<>();
    private final Set<UUID> toggledPlayers = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        enable(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        disable(event.getPlayer());
    }

    public boolean isDisabled(Player player) {
        return !toggledPlayers.contains(player.getUniqueId());
    }

    public boolean enable(Player player) {
        if (!player.hasPermission("MCT.ping")) {
            return false;
        }
        UUID uuid = player.getUniqueId();
        if (taskIds.containsKey(uuid)) {
            return true;
        }
        toggledPlayers.add(uuid);
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                MCT.getPluginInstanceVar(),
                () -> sendPing(player),
                0L,
                20L
        );
        taskIds.put(uuid, taskId);
        return true;
    }

    public void disable(Player player) {
        UUID uuid = player.getUniqueId();
        toggledPlayers.remove(uuid);
        Integer taskId = taskIds.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void sendPing(Player player) {
        if (!player.isOnline()) {
            disable(player);
            return;
        }
        if (isDisabled(player)) {
            disable(player);
            return;
        }
        int ping = player.getPing();
        ChatColor color;
        String display;
        if (ping <= 0) {
            color = ChatColor.RED;
            display = "Not Avl";
        } else if (ping <= 80) {
            color = ChatColor.GREEN;
            display = ping + " ms";
        } else if (ping <= 150) {
            color = ChatColor.YELLOW;
            display = ping + " ms";
        } else {
            color = ChatColor.RED;
            display = ping + " ms";
        }
        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(color + "Ping: " + display)
        );
    }
}
