package me.jitish.mCT.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Denchant implements CommandExecutor, TabCompleter {

    /**
     * Removes all enchantments from a single item.
     * @return the number of enchantments removed
     */
    private int stripEnchantments(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return 0;
        }
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        for (Enchantment ench : enchants.keySet()) {
            item.removeEnchantment(ench);
        }
        return enchants.size();
    }

    /**
     * Removes a specific enchantment from a single item.
     * @return true if the enchantment was present and removed
     */
    private boolean stripEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        if (item.containsEnchantment(enchantment)) {
            item.removeEnchantment(enchantment);
            return true;
        }
        return false;
    }

    /**
     * Removes all enchantments from every item in a player's inventory
     * (including armor and offhand).
     * @return the total number of enchantments removed
     */
    private int stripEntireInventory(Player target) {
        int removed = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            removed += stripEnchantments(item);
        }
        for (ItemStack item : target.getInventory().getArmorContents()) {
            removed += stripEnchantments(item);
        }
        removed += stripEnchantments(target.getInventory().getItemInOffHand());
        return removed;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }

        if (!executor.hasPermission("MCT.denchant")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to use this command."));
            return true;
        }

        // Usage: /denchant <player|all> [enchantment | all]
        if (args.length < 1 || args.length > 2) {
            executor.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW
                    + "/denchant <player|all> [enchantment | all]");
            executor.sendMessage(ChatColor.GRAY + "  No 2nd arg = remove all enchants from held item");
            executor.sendMessage(ChatColor.GRAY + "  <enchantment> = remove specific enchant from held item");
            executor.sendMessage(ChatColor.GRAY + "  all = remove all enchants from entire inventory");
            return true;
        }

        String playerArg = args[0];

        // --- Handle "all" player target ---
        if (playerArg.equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.denchant.toOtherPlayers")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lYou do not have permission to disenchant other players' items."));
                return true;
            }
            handleAllPlayers(executor, args);
            return true;
        }

        // --- Permission check for other players ---
        if (!playerArg.equalsIgnoreCase(executor.getName())
                && !executor.hasPermission("MCT.denchant.toOtherPlayers")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to disenchant other players' items."));
            return true;
        }

        // --- Resolve target player ---
        Player target = Bukkit.getPlayerExact(playerArg);
        if (target == null) {
            executor.sendMessage(ChatColor.RED + playerArg + ChatColor.WHITE + " is not online.");
            return true;
        }

        // --- /denchant <player>  →  strip all enchantments from held item ---
        if (args.length == 1) {
            ItemStack item = target.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                sendNoItemMessage(executor, target);
                return true;
            }

            int removed = stripEnchantments(item);
            if (removed == 0) {
                if (target.equals(executor)) {
                    executor.sendMessage(ChatColor.YELLOW + "Your held item has no enchantments.");
                } else {
                    executor.sendMessage(ChatColor.YELLOW + target.getName() + "'s held item has no enchantments.");
                }
            } else {
                if (target.equals(executor)) {
                    executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + removed
                            + ChatColor.GREEN + " enchantment(s) from your held item!");
                } else {
                    executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + removed
                            + ChatColor.GREEN + " enchantment(s) from " + ChatColor.YELLOW + target.getName()
                            + ChatColor.GREEN + "'s held item!");
                    target.sendMessage(ChatColor.GREEN + "All enchantments were removed from your held item!");
                }
            }
            return true;
        }

        // --- /denchant <player> all  →  strip entire inventory ---
        if (args[1].equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.denchant.all")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lYou do not have permission to disenchant entire inventories."));
                return true;
            }

            int removed = stripEntireInventory(target);
            if (removed == 0) {
                if (target.equals(executor)) {
                    executor.sendMessage(ChatColor.YELLOW + "No enchantments found in your inventory.");
                } else {
                    executor.sendMessage(ChatColor.YELLOW + "No enchantments found in " + target.getName() + "'s inventory.");
                }
            } else {
                if (target.equals(executor)) {
                    executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + removed
                            + ChatColor.GREEN + " enchantment(s) from your entire inventory!");
                } else {
                    executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + removed
                            + ChatColor.GREEN + " enchantment(s) from " + ChatColor.YELLOW + target.getName()
                            + ChatColor.GREEN + "'s entire inventory!");
                    target.sendMessage(ChatColor.GREEN + "All enchantments were removed from your entire inventory!");
                }
            }
            return true;
        }

        // --- /denchant <player> <enchantment>  →  strip specific enchantment from held item ---
        String enchantArg = args[1].toLowerCase();
        if (enchantArg.startsWith("minecraft:")) {
            enchantArg = enchantArg.substring("minecraft:".length());
        }
        Enchantment enchantment;
        try {
            enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArg));
        } catch (IllegalArgumentException e) {
            enchantment = null;
        }
        if (enchantment == null) {
            executor.sendMessage(ChatColor.RED + "Unknown enchantment: " + ChatColor.YELLOW + args[1]);
            executor.sendMessage(ChatColor.GRAY + "Use the minecraft key name (e.g. sharpness, efficiency, protection).");
            return true;
        }

        ItemStack item = target.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sendNoItemMessage(executor, target);
            return true;
        }

        String enchantName = formatEnchantmentName(enchantment);

        if (stripEnchantment(item, enchantment)) {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + enchantName
                        + ChatColor.GREEN + " from your held item!");
            } else {
                executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + enchantName
                        + ChatColor.GREEN + " from " + ChatColor.YELLOW + target.getName()
                        + ChatColor.GREEN + "'s held item!");
                target.sendMessage(ChatColor.GREEN + enchantName + " was removed from your held item!");
            }
        } else {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.YELLOW + "Your held item doesn't have " + enchantName + ".");
            } else {
                executor.sendMessage(ChatColor.YELLOW + target.getName() + "'s held item doesn't have " + enchantName + ".");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Arg 1: player names + "all"
            List<String> playerNames = new ArrayList<>();
            if (sender instanceof Player executor) {
                playerNames.add(executor.getName());
            }
            if (sender.hasPermission("MCT.denchant.toOtherPlayers")) {
                playerNames.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!playerNames.contains(player.getName())) {
                        playerNames.add(player.getName());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[0], playerNames, completions);

        } else if (args.length == 2) {
            // Arg 2: enchantment names + "all"
            List<String> options = new ArrayList<>();
            if (sender.hasPermission("MCT.denchant.all")) {
                options.add("all");
            }
            for (Enchantment enchantment : Enchantment.values()) {
                options.add(enchantment.getKey().getKey());
            }
            StringUtil.copyPartialMatches(args[1], options, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    /**
     * Handles /denchant all [...] — applies the action to every online player.
     */
    private void handleAllPlayers(Player executor, String[] args) {
        // /denchant all → strip held items for all players
        if (args.length == 1) {
            int totalRemoved = 0;
            int count = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                ItemStack item = target.getInventory().getItemInMainHand();
                int removed = stripEnchantments(item);
                if (removed > 0) {
                    count++;
                    totalRemoved += removed;
                    if (!target.equals(executor)) {
                        target.sendMessage(ChatColor.GREEN + "All enchantments were removed from your held item!");
                    }
                }
            }
            executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + totalRemoved
                    + ChatColor.GREEN + " enchantment(s) from " + ChatColor.YELLOW + count
                    + ChatColor.GREEN + " player(s)' held items!");
            return;
        }

        // /denchant all all → strip entire inventories for all players
        if (args[1].equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.denchant.all")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lYou do not have permission to disenchant entire inventories."));
                return;
            }
            int totalRemoved = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                totalRemoved += stripEntireInventory(target);
                if (!target.equals(executor)) {
                    target.sendMessage(ChatColor.GREEN + "All enchantments were removed from your entire inventory!");
                }
            }
            executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + totalRemoved
                    + ChatColor.GREEN + " enchantment(s) from all online players' inventories!");
            return;
        }

        // /denchant all <enchantment> → strip specific enchant from held items of all players
        String enchantArg = args[1].toLowerCase();
        if (enchantArg.startsWith("minecraft:")) {
            enchantArg = enchantArg.substring("minecraft:".length());
        }
        Enchantment enchantment;
        try {
            enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantArg));
        } catch (IllegalArgumentException e) {
            enchantment = null;
        }
        if (enchantment == null) {
            executor.sendMessage(ChatColor.RED + "Unknown enchantment: " + ChatColor.YELLOW + args[1]);
            executor.sendMessage(ChatColor.GRAY + "Use the minecraft key name (e.g. sharpness, efficiency, protection).");
            return;
        }

        String enchantName = formatEnchantmentName(enchantment);
        int count = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            ItemStack item = target.getInventory().getItemInMainHand();
            if (stripEnchantment(item, enchantment)) {
                count++;
                if (!target.equals(executor)) {
                    target.sendMessage(ChatColor.GREEN + enchantName + " was removed from your held item!");
                }
            }
        }
        executor.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + enchantName
                + ChatColor.GREEN + " from " + ChatColor.YELLOW + count + ChatColor.GREEN + " player(s)' held items!");
    }

    private void sendNoItemMessage(Player executor, Player target) {
        if (target.equals(executor)) {
            executor.sendMessage(ChatColor.RED + "You must hold an item in your main hand!");
        } else {
            executor.sendMessage(ChatColor.RED + target.getName() + " is not holding any item!");
        }
    }

    /**
     * Formats an enchantment key into a readable name.
     * e.g. "sharpness" -> "Sharpness", "fire_aspect" -> "Fire Aspect"
     */
    private String formatEnchantmentName(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        String[] parts = key.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
        }
        return sb.toString();
    }
}
