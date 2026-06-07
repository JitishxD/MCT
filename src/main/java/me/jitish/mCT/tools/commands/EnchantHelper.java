package me.jitish.mCT.tools.commands;

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class EnchantHelper {

    /**
     * Formats an enchantment key into a readable name.
     * e.g. "sharpness" -> "Sharpness", "fire_aspect" -> "Fire Aspect"
     */
    @SuppressWarnings("deprecation")
    public static String formatEnchantmentName(Enchantment enchantment) {
        String key = enchantment.getName();
        try {
            java.lang.reflect.Method getKey = enchantment.getClass().getMethod("getKey");
            Object nsKey = getKey.invoke(enchantment);
            java.lang.reflect.Method getNamespace = nsKey.getClass().getMethod("getKey");
            key = (String) getNamespace.invoke(nsKey);
        } catch (Exception e) {}

        key = key.toLowerCase();
        String[] parts = key.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return sb.toString();
    }

    public static Enchantment resolveEnchantment(String arg) {
        if (arg.toUpperCase().startsWith("MINECRAFT:")) {
            arg = arg.substring(10);
        }
        @SuppressWarnings("deprecation")
        Enchantment e = Enchantment.getByName(arg.toUpperCase());
        if (e != null) return e;

        try {
            Class<?> nsKeyClass = Class.forName("org.bukkit.NamespacedKey");
            java.lang.reflect.Method mcMethod = nsKeyClass.getMethod("minecraft", String.class);
            Object key = mcMethod.invoke(null, arg.toLowerCase());
            java.lang.reflect.Method getByKey = Enchantment.class.getMethod("getByKey", nsKeyClass);
            e = (Enchantment) getByKey.invoke(null, key);
            if (e != null) return e;
        } catch (Throwable ignored) {}

        String lower = arg.toLowerCase();
        switch (lower) {
            case "protection": return Enchantment.getByName("PROTECTION_ENVIRONMENTAL");
            case "fire_protection": return Enchantment.getByName("PROTECTION_FIRE");
            case "feather_falling": return Enchantment.getByName("PROTECTION_FALL");
            case "blast_protection": return Enchantment.getByName("PROTECTION_EXPLOSIONS");
            case "projectile_protection": return Enchantment.getByName("PROTECTION_PROJECTILE");
            case "respiration": return Enchantment.getByName("OXYGEN");
            case "aqua_affinity": return Enchantment.getByName("WATER_WORKER");
            case "thorns": return Enchantment.getByName("THORNS");
            case "depth_strider": return Enchantment.getByName("DEPTH_STRIDER");
            case "sharpness": return Enchantment.getByName("DAMAGE_ALL");
            case "smite": return Enchantment.getByName("DAMAGE_UNDEAD");
            case "bane_of_arthropods": return Enchantment.getByName("DAMAGE_ARTHROPODS");
            case "knockback": return Enchantment.getByName("KNOCKBACK");
            case "fire_aspect": return Enchantment.getByName("FIRE_ASPECT");
            case "looting": return Enchantment.getByName("LOOT_BONUS_MOBS");
            case "efficiency": return Enchantment.getByName("DIG_SPEED");
            case "silk_touch": return Enchantment.getByName("SILK_TOUCH");
            case "unbreaking": return Enchantment.getByName("DURABILITY");
            case "fortune": return Enchantment.getByName("LOOT_BONUS_BLOCKS");
            case "power": return Enchantment.getByName("ARROW_DAMAGE");
            case "punch": return Enchantment.getByName("ARROW_KNOCKBACK");
            case "flame": return Enchantment.getByName("ARROW_FIRE");
            case "infinity": return Enchantment.getByName("ARROW_INFINITE");
            case "luck_of_the_sea": return Enchantment.getByName("LUCK");
            case "lure": return Enchantment.getByName("LURE");
        }
        return null;
    }

    public static String getVanillaName(Enchantment e) {
        if (e.equals(Enchantment.getByName("PROTECTION_ENVIRONMENTAL"))) return "protection";
        if (e.equals(Enchantment.getByName("PROTECTION_FIRE"))) return "fire_protection";
        if (e.equals(Enchantment.getByName("PROTECTION_FALL"))) return "feather_falling";
        if (e.equals(Enchantment.getByName("PROTECTION_EXPLOSIONS"))) return "blast_protection";
        if (e.equals(Enchantment.getByName("PROTECTION_PROJECTILE"))) return "projectile_protection";
        if (e.equals(Enchantment.getByName("OXYGEN"))) return "respiration";
        if (e.equals(Enchantment.getByName("WATER_WORKER"))) return "aqua_affinity";
        if (e.equals(Enchantment.getByName("THORNS"))) return "thorns";
        if (e.equals(Enchantment.getByName("DEPTH_STRIDER"))) return "depth_strider";
        if (e.equals(Enchantment.getByName("DAMAGE_ALL"))) return "sharpness";
        if (e.equals(Enchantment.getByName("DAMAGE_UNDEAD"))) return "smite";
        if (e.equals(Enchantment.getByName("DAMAGE_ARTHROPODS"))) return "bane_of_arthropods";
        if (e.equals(Enchantment.getByName("KNOCKBACK"))) return "knockback";
        if (e.equals(Enchantment.getByName("FIRE_ASPECT"))) return "fire_aspect";
        if (e.equals(Enchantment.getByName("LOOT_BONUS_MOBS"))) return "looting";
        if (e.equals(Enchantment.getByName("DIG_SPEED"))) return "efficiency";
        if (e.equals(Enchantment.getByName("SILK_TOUCH"))) return "silk_touch";
        if (e.equals(Enchantment.getByName("DURABILITY"))) return "unbreaking";
        if (e.equals(Enchantment.getByName("LOOT_BONUS_BLOCKS"))) return "fortune";
        if (e.equals(Enchantment.getByName("ARROW_DAMAGE"))) return "power";
        if (e.equals(Enchantment.getByName("ARROW_KNOCKBACK"))) return "punch";
        if (e.equals(Enchantment.getByName("ARROW_FIRE"))) return "flame";
        if (e.equals(Enchantment.getByName("ARROW_INFINITE"))) return "infinity";
        if (e.equals(Enchantment.getByName("LUCK"))) return "luck_of_the_sea";
        if (e.equals(Enchantment.getByName("LURE"))) return "lure";
        return null;
    }

    public static List<String> getEnchantmentNames() {
        List<String> enchantNames = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            String bukkitName = enchantment.getName().toLowerCase();
            if (!enchantNames.contains(bukkitName)) enchantNames.add(bukkitName);

            String vanilla = getVanillaName(enchantment);
            if (vanilla != null && !enchantNames.contains(vanilla)) enchantNames.add(vanilla);

            try {
                java.lang.reflect.Method getKey = enchantment.getClass().getMethod("getKey");
                Object nsKey = getKey.invoke(enchantment);
                java.lang.reflect.Method getNamespace = nsKey.getClass().getMethod("getKey");
                String modernName = ((String) getNamespace.invoke(nsKey)).toLowerCase();
                if (!enchantNames.contains(modernName)) enchantNames.add(modernName);
            } catch (Exception ignored) {}
        }
        return enchantNames;
    }
}
