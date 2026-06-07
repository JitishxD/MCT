package me.jitish.mCT.tools.compatibility;

import org.bukkit.entity.Player;

public interface VersionHandler {
    void spawnParticle(Player p, String particleName);
    void setInvulnerable(Player p, boolean invulnerable);
    boolean isInvulnerable(Player p);
    void sendSound(Player p, String soundName, float volume, float pitch);
    void sendTpaRequestButtons(Player p, String prefixMessage);
    void sendActionBar(Player p, String message);
    int getPlayerPing(Player p);
    int getMinHeight(org.bukkit.World world);
}
