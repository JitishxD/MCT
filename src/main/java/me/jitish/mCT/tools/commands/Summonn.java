package me.jitish.mCT.tools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Summonn implements CommandExecutor, TabCompleter {

    private static final int MAX_COUNT = 500;

    private static final Map<String, SummonnVariants.EntityVariant> VARIANTS = SummonnVariants.getVariants();

    /**
     * Returns a sorted list of all spawnable entity type keys (lowercase).
     */
    private List<String> getSpawnableEntityNames() {
        List<String> names = new ArrayList<>();
        // Add custom variant aliases first
        names.addAll(VARIANTS.keySet());
        // Add all vanilla spawnable entities
        for (EntityType type : EntityType.values()) {
            if (type.isSpawnable() && type != EntityType.PLAYER) {
                names.add(type.getKey().getKey());
            }
        }
        Collections.sort(names);
        return names;
    }

    /**
     * Formats an entity key into a readable name.
     * e.g. "zombie_villager" -> "Zombie Villager"
     */
    private String formatEntityName(EntityType type) {
        String key = type.getKey().getKey();
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

    /**
     * Parses a single coordinate value, supporting:
     *   "~"    -> playerCoord + 0
     *   "~5"   -> playerCoord + 5
     *   "~-3"  -> playerCoord - 3
     *   "100"  -> 100 (absolute)
     */
    private double parseCoordinate(String input, double playerCoord) throws NumberFormatException {
        if (input.startsWith("~")) {
            String offset = input.substring(1);
            if (offset.isEmpty()) {
                return playerCoord;
            }
            return playerCoord + Double.parseDouble(offset);
        }
        return Double.parseDouble(input);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player executor)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }

        if (!executor.hasPermission("MCT.summonn")) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to use this command."));
            return true;
        }

        // Usage: /summonn <entity> [amount] [player | x y z]
        // Valid arg counts: 1, 2, 3 (player), 5 (coords)
        if (args.length < 1 || args.length == 4 || args.length > 5) {
            executor.sendMessage(ChatColor.RED + "Usage: " + ChatColor.YELLOW
                    + "/summonn <entity> [amount] [player | x y z]");
            executor.sendMessage(ChatColor.GRAY + "  Coordinates support ~ for relative (e.g. ~ ~5 ~)");
            return true;
        }

        // --- Resolve entity type (check aliases first, then vanilla) ---
        String entityArg = args[0].toLowerCase();
        if (entityArg.startsWith("minecraft:")) {
            entityArg = entityArg.substring("minecraft:".length());
        }

        SummonnVariants.EntityVariant variant = VARIANTS.get(entityArg);
        EntityType entityType = null;
        String entityName;

        if (variant != null) {
            entityType = variant.baseType();
            entityName = variant.displayName();
        } else {
            for (EntityType type : EntityType.values()) {
                if (type.isSpawnable() && type != EntityType.PLAYER
                        && type.getKey().getKey().equalsIgnoreCase(entityArg)) {
                    entityType = type;
                    break;
                }
            }
            if (entityType == null) {
                executor.sendMessage(ChatColor.RED + "Unknown entity: " + ChatColor.YELLOW + args[0]);
                executor.sendMessage(ChatColor.GRAY + "Use the minecraft key name (e.g. zombie, creeper, charged_creeper).");
                return true;
            }
            entityName = formatEntityName(entityType);
        }

        // --- Parse amount (default 1) ---
        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                executor.sendMessage(ChatColor.RED + "Amount must be a number between 1 and " + MAX_COUNT + ".");
                return true;
            }
            if (amount < 1 || amount > MAX_COUNT) {
                executor.sendMessage(ChatColor.RED + "Amount must be between 1 and " + MAX_COUNT + ".");
                return true;
            }
        }

        // --- Resolve spawn location ---
        Location spawnLocation;
        String locationDesc;

        if (args.length == 5) {
            // --- Coordinates: /summonn <entity> <amount> <x> <y> <z> ---
            double x, y, z;
            try {
                x = parseCoordinate(args[2], executor.getLocation().getX());
                y = parseCoordinate(args[3], executor.getLocation().getY());
                z = parseCoordinate(args[4], executor.getLocation().getZ());
            } catch (NumberFormatException e) {
                executor.sendMessage(ChatColor.RED + "Invalid coordinates! Use numbers or ~ for relative.");
                return true;
            }

            World world = executor.getWorld();
            spawnLocation = new Location(world, x, y, z);
            locationDesc = ChatColor.YELLOW + String.format("%.1f, %.1f, %.1f", x, y, z) + ChatColor.GREEN;

        } else if (args.length == 3) {
            // --- Player target: /summonn <entity> <amount> <player> ---
            if (!executor.hasPermission("MCT.summonn.toOtherPlayers")) {
                executor.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lYou do not have permission to summon entities at other players."));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                executor.sendMessage(ChatColor.RED + args[2] + ChatColor.WHITE + " is not online.");
                return true;
            }

            spawnLocation = target.getLocation();
            locationDesc = target.equals(executor) ? "your location"
                    : ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "'s location";

            // Notify the target player if it's someone else
            if (!target.equals(executor)) {
                target.sendMessage(ChatColor.GREEN + "" + ChatColor.AQUA + amount + "x "
                        + entityName + ChatColor.GREEN + " was summoned at your location!");
            }

        } else {
            // --- Self: /summonn <entity> [amount] ---
            spawnLocation = executor.getLocation();
            locationDesc = "your location";
        }

        // --- Spawn entities ---
        for (int i = 0; i < amount; i++) {
            Entity spawned = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
            if (variant != null) {
                variant.modifier().accept(spawned);
            }
        }

        executor.sendMessage(ChatColor.GREEN + "Summoned " + ChatColor.AQUA + amount + "x "
                + entityName + ChatColor.GREEN + " at " + locationDesc + "!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Arg 1: entity types
            StringUtil.copyPartialMatches(args[0], getSpawnableEntityNames(), completions);

        } else if (args.length == 2) {
            // Arg 2: amount suggestions
            List<String> amounts = List.of("1", "2", "5", "10", "25", "50", "100");
            StringUtil.copyPartialMatches(args[1], amounts, completions);

        } else if (args.length == 3) {
            // Arg 3: player names OR x-coordinate
            List<String> options = new ArrayList<>();
            options.add("~");
            if (sender.hasPermission("MCT.summonn.toOtherPlayers")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    options.add(player.getName());
                }
            }
            StringUtil.copyPartialMatches(args[2], options, completions);

        } else if (args.length == 4) {
            // Arg 4: y-coordinate (only if arg 3 looks like a coordinate)
            if (isCoordinate(args[2])) {
                completions.add("~");
            }

        } else if (args.length == 5) {
            // Arg 5: z-coordinate (only if arg 3 and 4 look like coordinates)
            if (isCoordinate(args[2]) && isCoordinate(args[3])) {
                completions.add("~");
            }
        }

        Collections.sort(completions);
        return completions;
    }

    /**
     * Checks if a string looks like a coordinate value (number or ~ notation).
     */
    private boolean isCoordinate(String input) {
        if (input.startsWith("~")) {
            String offset = input.substring(1);
            if (offset.isEmpty()) return true;
            try {
                Double.parseDouble(offset);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
