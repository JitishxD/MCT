package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class Enchantt implements CommandExecutor, TabCompleter {

    private static final int MAX_LEVEL = 255;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }
        Player executor = (Player) sender;

        if (!executor.hasPermission("MCT.enchantt")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to use this command."));
            return true;
        }

        // Usage: /enchantt <player> <enchantment> <level>
        if (args.length != 3) {
            executor.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/enchantt <player> <enchantment> <level>");
            return true;
        }

        // --- Resolve enchantment first (shared for single & all) ---
        Enchantment enchantment = EnchantHelper.resolveEnchantment(args[1]);
        if (enchantment == null) {
            executor.sendMessage(ChatColor.RED + "Unknown enchantment: " + ChatColor.YELLOW + args[1]);
            executor.sendMessage(ChatColor.GRAY + "Use the vanilla name (e.g. sharpness) or bukkit name (e.g. DAMAGE_ALL).");
            return true;
        }

        // --- Parse and validate level ---
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            executor.sendMessage(ChatColor.RED + "Level must be a number between 1 and " + MAX_LEVEL + ".");
            return true;
        }

        if (level < 1 || level > MAX_LEVEL) {
            executor.sendMessage(ChatColor.RED + "Level must be between 1 and " + MAX_LEVEL + ".");
            return true;
        }

        String enchantName = EnchantHelper.formatEnchantmentName(enchantment);
        String playerArg = args[0];

        // --- Handle "all" target ---
        if (playerArg.equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.enchantt.toOtherPlayers")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lYou do not have permission to enchant other players' items."));
                return true;
            }

            int count = 0;
            int skipped = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                ItemStack item = target.getInventory().getItemInHand();
                if (item.getType() == Material.AIR) {
                    skipped++;
                    continue;
                }
                item.addUnsafeEnchantment(enchantment, level);
                count++;
                if (!target.equals(executor)) {
                    target.sendMessage(ChatColor.GREEN + "Your held item was enchanted with " + ChatColor.AQUA
                            + enchantName + " " + level + ChatColor.GREEN + "!");
                }
            }
            executor.sendMessage(ChatColor.GREEN + "Applied " + ChatColor.AQUA + enchantName + " " + level
                    + ChatColor.GREEN + " to " + ChatColor.YELLOW + count + ChatColor.GREEN + " player(s)!"
                    + (skipped > 0 ? ChatColor.GRAY + " (" + skipped + " not holding items)" : ""));
            return true;
        }

        // --- Single player target ---
        if (!playerArg.equalsIgnoreCase(executor.getName())
                && !executor.hasPermission("MCT.enchantt.toOtherPlayers")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to enchant other players' items."));
            return true;
        }

        Player target = Bukkit.getPlayerExact(playerArg);
        if (target == null) {
            executor.sendMessage(ChatColor.RED + playerArg + ChatColor.WHITE + " is not online.");
            return true;
        }

        // --- Validate held item ---
        ItemStack item = target.getInventory().getItemInHand();
        if (item.getType() == Material.AIR) {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.RED + "You must hold an item in your main hand!");
            } else {
                executor.sendMessage(ChatColor.RED + target.getName() + " is not holding any item!");
            }
            return true;
        }

        // --- Apply enchantment (unsafe to bypass vanilla limits) ---
        item.addUnsafeEnchantment(enchantment, level);

        if (target.equals(executor)) {
            executor.sendMessage(ChatColor.GREEN + "Applied " + ChatColor.AQUA + enchantName + " " + level
                    + ChatColor.GREEN + " to your held item!");
        } else {
            executor.sendMessage(ChatColor.GREEN + "Applied " + ChatColor.AQUA + enchantName + " " + level
                    + ChatColor.GREEN + " to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "'s held item!");
            target.sendMessage(ChatColor.GREEN + "Your held item was enchanted with " + ChatColor.AQUA
                    + enchantName + " " + level + ChatColor.GREEN + "!");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Arg 1: player names + "all"
            List<String> playerNames = new ArrayList<>();
            if (sender instanceof Player) {
                Player executor = (Player) sender;
                playerNames.add(executor.getName());
            }
            if (sender.hasPermission("MCT.enchantt.toOtherPlayers")) {
                playerNames.add("all");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!playerNames.contains(player.getName())) {
                        playerNames.add(player.getName());
                    }
                }
            }
            StringUtil.copyPartialMatches(args[0], playerNames, completions);

        } else if (args.length == 2) {
            // Arg 2: enchantment names (minecraft keys)
            List<String> enchantNames = EnchantHelper.getEnchantmentNames();
            StringUtil.copyPartialMatches(args[1], enchantNames, completions);

        } else if (args.length == 3) {
            // Arg 3: level suggestions
            List<String> levels = java.util.Arrays.asList("1", "5", "10", "50", "100", "255");
            StringUtil.copyPartialMatches(args[2], levels, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
