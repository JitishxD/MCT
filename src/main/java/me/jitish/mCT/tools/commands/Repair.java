package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repair implements CommandExecutor, TabCompleter {

    @SuppressWarnings("deprecation")
    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        if (item.getType().getMaxDurability() > 0 && item.getDurability() > 0) {
            item.setDurability((short) 0);
            return true;
        }
        return false;
    }

    private int repairInventory(Player target) {
        int repairedCount = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (repairItem(item)) {
                repairedCount++;
            }
        }
        for (ItemStack item : target.getInventory().getArmorContents()) {
            if (repairItem(item)) {
                repairedCount++;
            }
        }
        try {
            if (repairItem(target.getInventory().getItemInOffHand())) {
                repairedCount++;
            }
        } catch (NoSuchMethodError ignored) {}
        return repairedCount;
    }

    private boolean repairHand(Player target) {
        ItemStack handItem = target.getInventory().getItemInHand();
        if (handItem.getType() == Material.AIR) {
            return false;
        }
        @SuppressWarnings("deprecation")
        boolean damageable = handItem.getType().getMaxDurability() > 0;
        if (!damageable) {
            return false;
        }
        return repairItem(handItem);
    }

    private void repairHand(Player executor, Player target) {
        if (target.getInventory().getItemInHand().getType() == Material.AIR) {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.RED + "You must hold an item in your main hand!");
            } else {
                executor.sendMessage(ChatColor.RED + target.getName() + " is not holding an item in their main hand!");
            }
            return;
        }

        @SuppressWarnings("deprecation")
        boolean damageable = target.getInventory().getItemInHand().getType().getMaxDurability() > 0;
        if (!damageable) {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.RED + "This item cannot be repaired!");
            } else {
                executor.sendMessage(ChatColor.RED + "The item in " + target.getName() + "'s hand cannot be repaired!");
            }
            return;
        }

        if (repairHand(target)) {
            if (target.equals(executor)) {
                executor.sendMessage(ChatColor.GREEN + "Your item has been repaired!");
            } else {
                target.sendMessage(ChatColor.GREEN + "Your held item has been repaired!");
                executor.sendMessage(ChatColor.GREEN + "Repaired the item in " + target.getName() + "'s hand.");
            }
        } else if (target.equals(executor)) {
            executor.sendMessage(ChatColor.YELLOW + "This item is already at full durability.");
        } else {
            executor.sendMessage(ChatColor.YELLOW + target.getName() + "'s held item is already at full durability.");
        }
    }

    private void repairInventory(Player executor, Player target) {
        int repairedCount = repairInventory(target);
        if (repairedCount > 0) {
            target.sendMessage(ChatColor.GREEN + "Repaired " + ChatColor.AQUA + repairedCount
                    + ChatColor.GREEN + " item(s) in your inventory!");
            if (!target.equals(executor)) {
                executor.sendMessage(ChatColor.GREEN + "Repaired " + ChatColor.AQUA + repairedCount
                        + ChatColor.GREEN + " item(s) in " + target.getName() + "'s inventory.");
            }
        } else if (target.equals(executor)) {
            executor.sendMessage(ChatColor.YELLOW + "No items needed repairing.");
        } else {
            executor.sendMessage(ChatColor.YELLOW + "No items needed repairing in " + target.getName() + "'s inventory.");
        }
    }

    private boolean requiresOtherPlayersPermission(Player executor, String targetArg) {
        return targetArg.equalsIgnoreCase("all")
                || !targetArg.equalsIgnoreCase(executor.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }
        Player executor = (Player) sender;

        if (!executor.hasPermission("MCT.repair")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            repairHand(executor, executor);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                if (!executor.hasPermission("MCT.repair.all")) {
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair all items."));
                    return true;
                }
                repairInventory(executor, executor);
                return true;
            }

            if (requiresOtherPlayersPermission(executor, args[0])
                    && !executor.hasPermission("MCT.repair.toOtherPlayers")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair other players."));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                executor.sendMessage(ChatColor.RED + args[0] + ChatColor.WHITE + " is not online.");
                return true;
            }
            repairHand(executor, target);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("all") && args[1].equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.repair.toOtherPlayers")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair other players."));
                return true;
            }
            if (!executor.hasPermission("MCT.repair.all")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair all items."));
                return true;
            }

            int totalRepaired = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                totalRepaired += repairInventory(player);
            }
            executor.sendMessage(ChatColor.GREEN + "Repaired " + ChatColor.AQUA + totalRepaired
                    + ChatColor.GREEN + " item(s) across all online players!");
            return true;
        }

        if (args[args.length - 1].equalsIgnoreCase("all")) {
            if (!executor.hasPermission("MCT.repair.all")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair all items."));
                return true;
            }

            for (int i = 0; i < args.length - 1; i++) {
                String targetArg = args[i];
                if (requiresOtherPlayersPermission(executor, targetArg)
                        && !executor.hasPermission("MCT.repair.toOtherPlayers")) {
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair other players."));
                    return true;
                }

                if (targetArg.equalsIgnoreCase("all")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        repairInventory(executor, player);
                    }
                    executor.sendMessage(ChatColor.GREEN + "Repaired all items for all online players!");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(targetArg);
                if (target == null) {
                    executor.sendMessage(ChatColor.RED + targetArg + ChatColor.WHITE + " is not online.");
                    continue;
                }
                repairInventory(executor, target);
            }
            return true;
        }

        if (!executor.hasPermission("MCT.repair.toOtherPlayers")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to repair other players."));
            return true;
        }

        for (String targetArg : args) {
            Player target = Bukkit.getPlayerExact(targetArg);
            if (target == null) {
                executor.sendMessage(ChatColor.RED + targetArg + ChatColor.WHITE + " is not online.");
                continue;
            }
            repairHand(executor, target);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return Collections.emptyList();
        }

        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("MCT.repair.all")) {
                options.add("all");
            }
            if (sender.hasPermission("MCT.repair.toOtherPlayers")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    options.add(player.getName());
                }
            }
        } else if (sender.hasPermission("MCT.repair.all")) {
            options.add("all");
        }

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], options, completions);
        Collections.sort(completions);
        return completions;
    }
}
