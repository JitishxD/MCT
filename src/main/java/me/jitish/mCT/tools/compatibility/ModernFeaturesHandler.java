package me.jitish.mCT.tools.compatibility;

import me.jitish.mCT.tools.compatibility.VersionHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ModernFeaturesHandler implements VersionHandler {

    @Override
    public void spawnParticle(Player p, String particleName) {
        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            p.getWorld().spawnParticle(particle, p.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5);
        } catch (IllegalArgumentException ignored) {
            // Invalid particle name in config
        }
    }

    @Override
    public void setInvulnerable(Player p, boolean invulnerable) {
        p.setInvulnerable(invulnerable);
    }

    @Override
    public boolean isInvulnerable(Player p) {
        return p.isInvulnerable();
    }

    @Override
    public void sendSound(Player p, String soundName, float volume, float pitch) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            p.playSound(p.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            // Invalid sound name in config
        }
    }

    @Override
    public void sendTpaRequestButtons(Player p, String prefixMessage) {
        TextComponent acceptBtn = new TextComponent(ChatColor.GREEN + "[Accept] ");
        acceptBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        acceptBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept")));

        TextComponent denyBtn = new TextComponent(ChatColor.RED + "[Deny]");
        denyBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        denyBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to deny")));

        TextComponent msg = new TextComponent(prefixMessage);
        msg.addExtra(acceptBtn);
        msg.addExtra(denyBtn);

        p.spigot().sendMessage(msg);
    }

    @Override
    public void sendActionBar(Player p, String message) {
        p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public int getPlayerPing(Player p) {
        return p.getPing();
    }

    @Override
    public int getMinHeight(org.bukkit.World world) {
        return world.getMinHeight();
    }
}
