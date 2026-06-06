package me.jitish.mCT.tpa;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Reads TPA configuration from config.yml under the "tpa" section.
 * All values have sensible defaults so the plugin works even with an empty config.
 */
public class TpaSettings {

    private final JavaPlugin plugin;

    public TpaSettings(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ── Timing ───────────────────────────────────────────────────

    public int getRequestTimeout() {
        return plugin.getConfig().getInt("tpa.request-timeout", 60);
    }

    public int getTeleportDelay() {
        return plugin.getConfig().getInt("tpa.teleport-delay", 5);
    }

    public int getBackDelay() {
        return plugin.getConfig().getInt("tpa.back-delay", 5);
    }

    public int getInvincibilityDuration() {
        return plugin.getConfig().getInt("tpa.invincibility-duration", 5);
    }

    // ── Cooldowns ────────────────────────────────────────────────

    public int getTpaCooldown() {
        return plugin.getConfig().getInt("tpa.cooldowns.tpa", 10);
    }

    public int getTpaHereCooldown() {
        return plugin.getConfig().getInt("tpa.cooldowns.tpahere", 10);
    }

    public int getTpaHereAllCooldown() {
        return plugin.getConfig().getInt("tpa.cooldowns.tpahereall", 10);
    }

    public int getBackCooldown() {
        return plugin.getConfig().getInt("tpa.cooldowns.back", 10);
    }

    // ── Feature toggles (default: true = enabled) ────────────────

    public boolean isBackEnabled() {
        return plugin.getConfig().getBoolean("tpa.features.back-command", true);
    }

    public boolean isBackOnDeath() {
        return plugin.getConfig().getBoolean("tpa.features.back-on-death", true);
    }

    public boolean isBackOnQuit() {
        return plugin.getConfig().getBoolean("tpa.features.back-on-quit", true);
    }

    public boolean isTeleportLogging() {
        return plugin.getConfig().getBoolean("tpa.features.teleport-logging", false);
    }

    public boolean isMoveCancel() {
        return plugin.getConfig().getBoolean("tpa.features.move-cancel", true);
    }

    public boolean isParticlesEnabled() {
        return plugin.getConfig().getBoolean("tpa.features.particles", true);
    }

    public boolean isSoundsEnabled() {
        return plugin.getConfig().getBoolean("tpa.features.sounds", true);
    }

    public boolean isClickableButtons() {
        return plugin.getConfig().getBoolean("tpa.features.clickable-buttons", true);
    }

    public boolean isInvincibilityEnabled() {
        return plugin.getConfig().getBoolean("tpa.features.invincibility", true);
    }

    public boolean isSafeTeleport() {
        return plugin.getConfig().getBoolean("tpa.features.safe-teleport", true);
    }

    public boolean isInterdimensionalTravel() {
        return plugin.getConfig().getBoolean("tpa.features.interdimensional-travel", true);
    }

    public boolean isGamemodeCheck() {
        return plugin.getConfig().getBoolean("tpa.features.gamemode-check", true);
    }

    // ── Safe teleport ────────────────────────────────────────────

    public int getSafeTeleportRadius() {
        return plugin.getConfig().getInt("tpa.safe-teleport.radius", 5);
    }

    public int getSafeTeleportVerticalCap() {
        return plugin.getConfig().getInt("tpa.safe-teleport.vertical-cap", 64);
    }

    // ── Sound & Particles ────────────────────────────────────────

    public Sound getSound() {
        String name = plugin.getConfig().getString("tpa.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
        try {
            return Sound.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }

    public float getSoundVolume() {
        return (float) plugin.getConfig().getDouble("tpa.sound-volume", 3.0);
    }

    public float getSoundPitch() {
        return (float) plugin.getConfig().getDouble("tpa.sound-pitch", 0.533);
    }

    public Particle getParticle() {
        String name = plugin.getConfig().getString("tpa.particle", "POOF");
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Particle.POOF;
        }
    }

    // ── Disabled worlds ──────────────────────────────────────────

    public List<String> getDisabledWorlds() {
        return plugin.getConfig().getStringList("tpa.disabled-worlds");
    }

    public boolean isWorldDisabled(String worldName) {
        for (String disabled : getDisabledWorlds()) {
            if (disabled.equalsIgnoreCase(worldName)) {
                return true;
            }
        }
        return false;
    }

    // ── Messages ─────────────────────────────────────────────────

    public String getPrefix() {
        String prefix = plugin.getConfig().getString("tpa.messages.prefix", "&c[&6MCT&c]");
        if (prefix.equalsIgnoreCase("none")) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix) + " ";
    }

    /**
     * Gets a message from config, translates color codes.
     * Returns null if the message is set to "none" (disabled).
     */
    public String getMessage(String key) {
        String msg = plugin.getConfig().getString("tpa.messages." + key);
        if (msg == null || msg.equalsIgnoreCase("none")) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Gets and formats a message, replacing %player% with the player's name.
     * Returns null if the message is disabled.
     */
    public String formatMessage(String key, Player player) {
        String msg = getMessage(key);
        if (msg == null) {
            return null;
        }
        return msg.replace("%player%", player.getName());
    }

    /**
     * Gets and formats a message with custom placeholder pairs.
     * Usage: formatMessage("key", "%player%", "Steve", "%time%", "60")
     * Returns null if the message is disabled.
     */
    public String formatMessage(String key, String... replacements) {
        String msg = getMessage(key);
        if (msg == null) {
            return null;
        }
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }
}
