package me.jitish.mCT.tools.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ColorCodesDemo implements Listener {
    public String lastJoinedTime(PlayerJoinEvent event) {
        long currentTime = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            long timeDifference = currentTime - player.getLastPlayed();
            long days = timeDifference / 86400000L;
            long hours = timeDifference / 3600000L % 24L;
            long minutes = timeDifference / 60000L % 60L;
            long seconds = timeDifference / 1000L % 60L;
            return "You joined back after " + days + " days, " + hours + " hours, " + minutes + " minutes, and " + seconds + " seconds.";
        } else {
            return "You have joined our sever first time";
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            player.sendMessage(this.lastJoinedTime(event));
            event.setJoinMessage("§6§l§kob " + ChatColor.RED + "Welcome back " + player.getDisplayName() + " §r§6§l§oto Minecraft Server §kob");
        } else {
            event.setJoinMessage("§6§l§kob " + ChatColor.RED + "Welcome " + player.getDisplayName() + " §r§6§l§oto Minecraft Server §kob");
        }

    }
}
