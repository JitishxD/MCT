package me.jitish.mCT.tools.compatibility;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LegacyFeaturesHandler implements VersionHandler, Listener {

    private final Set<UUID> godModePlayers = new HashSet<>();

    @Override
    public void spawnParticle(Player p, String particleName) {
        // No-op or basic fallback for older versions
    }

    @Override
    public void setInvulnerable(Player p, boolean invulnerable) {
        if (invulnerable) {
            godModePlayers.add(p.getUniqueId());
        } else {
            godModePlayers.remove(p.getUniqueId());
        }
    }

    @Override
    public boolean isInvulnerable(Player p) {
        return godModePlayers.contains(p.getUniqueId());
    }

    @Override
    public void sendSound(Player p, String soundName, float volume, float pitch) {
        try {
            String name = soundName.toUpperCase();
            if (name.equals("ENTITY_PLAYER_LEVELUP")) {
                name = "LEVEL_UP";
            }
            java.lang.reflect.Method valueOf = org.bukkit.Sound.class.getMethod("valueOf", String.class);
            org.bukkit.Sound sound = (org.bukkit.Sound) valueOf.invoke(null, name);
            p.playSound(p.getLocation(), sound, volume, pitch);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void sendTpaRequestButtons(Player p, String prefixMessage) {
        TextComponent acceptBtn = new TextComponent(ChatColor.GREEN + "[Accept] ");
        acceptBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        acceptBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));

        TextComponent denyBtn = new TextComponent(ChatColor.RED + "[Deny]");
        denyBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        denyBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny").create()));

        TextComponent msg = new TextComponent(prefixMessage);
        msg.addExtra(acceptBtn);
        msg.addExtra(denyBtn);

        p.spigot().sendMessage(msg);
    }

    @Override
    public void sendActionBar(Player p, String message) {
        try {
            String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Object handle = p.getClass().getMethod("getHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            
            Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Object chatComponent = chatSerializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
            Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
            Object packet = packetClass.getConstructor(iChatBaseComponentClass, byte.class).newInstance(chatComponent, (byte) 2);
            
            playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Override
    public int getPlayerPing(Player p) {
        try {
            return p.getPing();
        } catch (Throwable t) {
            try {
                Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
                return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
            } catch (Exception e) {
                return -1;
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (godModePlayers.contains(p.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public int getMinHeight(org.bukkit.World world) {
        return 0; // Legacy worlds always start at 0
    }

    @Override
    public String getLockSymbol() {
        return "\u2588";
    }
}
