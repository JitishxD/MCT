package me.jitish.mCT.commands;

import me.jitish.mCT.warps.WarpPoint;
import me.jitish.mCT.warps.WarpStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WarpCommand implements CommandExecutor, TabCompleter {
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 24;

    private final WarpStore store;
    private final Settings settings;

    public WarpCommand(WarpStore store, Settings settings) {
        this.store = store;
        this.settings = settings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(settings.basePermission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "create":
                createWarp(sender, label, args);
                return true;
            case "delete":
                removeWarp(sender, label, args);
                return true;
            case "list":
                listWarps(sender, label, args);
                return true;
            case "info":
                sendWarpInfo(sender, label, args);
                return true;
            case "go":
                teleportUsingGo(sender, label, args);
                return true;
            case "private":
                togglePrivate(sender, label, args);
                return true;
            case "allow":
                allowPlayer(sender, label, args);
                return true;
            case "deny":
                denyPlayer(sender, label, args);
                return true;
            case "allowed":
                listAllowed(sender, label, args);
                return true;
            default:
                teleportToWarp(sender, args[0]);
                return true;
        }
    }

    private void createWarp(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can create " + settings.warpLabelPlural + " because a location is needed.");
            return;
        }

        if (!player.hasPermission(settings.createPermission)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to create " + settings.warpLabelPlural + "."));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " create <name>");
            return;
        }

        String name = args[1];
        if (!isValidWarpName(name)) {
            player.sendMessage(ChatColor.RED + "Warp names must be 3-24 characters and use only letters, numbers, _ or -.");
            return;
        }

        if (store.exists(name)) {
            player.sendMessage(ChatColor.RED + "A " + settings.warpLabel + " with that name already exists.");
            return;
        }

        store.create(player, name);
        player.sendMessage(ChatColor.GREEN + "Created " + settings.warpLabel + " " + ChatColor.AQUA + name + ChatColor.GREEN + ".");
    }

    private void removeWarp(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(settings.removePermission)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lYou do not have permission to remove " + settings.warpLabelPlural + "."));
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " delete <name>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        if (settings.ownerCanRemove
                && sender instanceof Player player
                && !player.getUniqueId().equals(warp.getOwnerId())
                && !player.hasPermission(settings.adminPermission)) {
            player.sendMessage(ChatColor.RED + "You can only remove your own " + settings.warpLabelPlural + ".");
            return;
        }

        store.remove(args[1]);
        sender.sendMessage(ChatColor.GREEN + "Removed " + settings.warpLabel + " " + ChatColor.AQUA + warp.getName() + ChatColor.GREEN + ".");
    }

    private void togglePrivate(CommandSender sender, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " private <name>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        if (!canManageWarp(sender, warp)) {
            sender.sendMessage(ChatColor.RED + "You can only manage privacy of your own " + settings.warpLabelPlural + ".");
            return;
        }

        warp.setPrivate(!warp.isPrivate());
        store.save();

        if (warp.isPrivate()) {
            sender.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.AQUA + warp.getName()
                    + ChatColor.GREEN + " is now " + ChatColor.RED + "\u2588 Private" + ChatColor.GREEN + ".");
            sender.sendMessage(ChatColor.GRAY + "Use /" + label + " allow " + warp.getName()
                    + " <player> to grant access.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.AQUA + warp.getName()
                    + ChatColor.GREEN + " is now " + ChatColor.GREEN + "\u2588 Public" + ChatColor.GREEN + ".");
        }
    }

    private void allowPlayer(CommandSender sender, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " allow <warp> <player>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        if (!canManageWarp(sender, warp)) {
            sender.sendMessage(ChatColor.RED + "You can only manage access of your own " + settings.warpLabelPlural + ".");
            return;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[2] + "' has never joined this server.");
            return;
        }

        if (target.getUniqueId().equals(warp.getOwnerId())) {
            sender.sendMessage(ChatColor.YELLOW + "The warp owner always has access.");
            return;
        }

        if (warp.addAllowedPlayer(target.getUniqueId())) {
            store.save();
            sender.sendMessage(ChatColor.GREEN + "Granted " + ChatColor.AQUA + target.getName()
                    + ChatColor.GREEN + " access to warp " + ChatColor.AQUA + warp.getName() + ChatColor.GREEN + ".");
        } else {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " already has access to that warp.");
        }
    }

    private void denyPlayer(CommandSender sender, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " deny <warp> <player>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        if (!canManageWarp(sender, warp)) {
            sender.sendMessage(ChatColor.RED + "You can only manage access of your own " + settings.warpLabelPlural + ".");
            return;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

        if (warp.removeAllowedPlayer(target.getUniqueId())) {
            store.save();
            sender.sendMessage(ChatColor.GREEN + "Revoked " + ChatColor.AQUA + target.getName()
                    + ChatColor.GREEN + "'s access to warp " + ChatColor.AQUA + warp.getName() + ChatColor.GREEN + ".");
        } else {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " did not have access to that warp.");
        }
    }

    private void listAllowed(CommandSender sender, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " allowed <warp>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        if (!canManageWarp(sender, warp)) {
            sender.sendMessage(ChatColor.RED + "You can only view access lists of your own " + settings.warpLabelPlural + ".");
            return;
        }

        String status = warp.isPrivate()
                ? (ChatColor.RED + "\u2588 Private")
                : (ChatColor.GREEN + "\u2588 Public");
        sender.sendMessage(ChatColor.GOLD + "Warp " + ChatColor.AQUA + warp.getName()
                + ChatColor.GOLD + " - " + status);

        Set<UUID> allowed = warp.getAllowedPlayers();
        if (allowed.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "No players have been explicitly allowed.");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Allowed players (" + allowed.size() + "):");
            for (UUID playerId : allowed) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
                String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerId.toString();
                sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + playerName);
            }
        }
    }

    /**
     * Returns true if the sender can manage the given warp's privacy/access settings.
     * The owner always can. Admins (with adminPermission) always can. Others cannot.
     */
    private boolean canManageWarp(CommandSender sender, WarpPoint warp) {
        if (sender.hasPermission(settings.adminPermission)) {
            return true;
        }
        if (sender instanceof Player player) {
            return player.getUniqueId().equals(warp.getOwnerId());
        }
        return true; // console can always manage
    }

    private void listWarps(CommandSender sender, String label, String[] args) {
        if (args.length > 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " list [page]");
            return;
        }

        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " list [page]");
                return;
            }
        }

        List<WarpPoint> warps = store.all();
        if (warps.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "There are no " + settings.warpLabelPlural + " yet.");
            return;
        }

        int pageSize = Math.max(1, settings.listPageSize);
        int totalPages = (int) Math.ceil((double) warps.size() / pageSize);
        if (page < 1 || page > totalPages) {
            sender.sendMessage(ChatColor.RED + "Invalid page. Choose between 1 and " + totalPages + ".");
            return;
        }

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, warps.size());

        // Title bar
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6&l\u2B50 " + settings.title + " &8&l\u2502 &7Page &f" + page + "&7/&f" + totalPages
                        + " &8(&7" + warps.size() + " total&8)"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&8&m                                                  "));

        // Compact inline rows
        for (int i = startIndex; i < endIndex; i++) {
            WarpPoint warp = warps.get(i);
            int displayIndex = i + 1;

            StringBuilder row = new StringBuilder();

            // Index
            row.append(ChatColor.GOLD).append(displayIndex).append(". ");

            // Name (green for public, gray+lock for private)
            if (warp.isPrivate()) {
                row.append(ChatColor.GRAY).append("\uD83D\uDD12 ")
                        .append(ChatColor.ITALIC).append(warp.getName())
                        .append(ChatColor.RESET);
            } else {
                row.append(ChatColor.GREEN).append(warp.getName());
            }

            // Owner (if shown)
            if (settings.showOwnerInList) {
                row.append(ChatColor.DARK_GRAY).append(" \u00BB ")
                        .append(ChatColor.YELLOW).append(warp.getOwnerName());
            }

            // Visits
            int visits = warp.getVisits();
            row.append(ChatColor.DARK_GRAY).append(" \u2022 ")
                    .append(ChatColor.AQUA).append(visits)
                    .append(ChatColor.DARK_AQUA).append(visits == 1 ? " visit" : " visits");

            // Access indicator (only for private warps)
            if (warp.isPrivate()) {
                boolean hasAccess = true;
                if (sender instanceof Player player) {
                    hasAccess = warp.canAccess(player.getUniqueId())
                            || player.hasPermission(settings.adminPermission);
                }
                if (hasAccess) {
                    row.append(ChatColor.DARK_GRAY).append(" \u2022 ")
                            .append(ChatColor.YELLOW).append("\u2714 Allowed");
                } else {
                    row.append(ChatColor.DARK_GRAY).append(" \u2022 ")
                            .append(ChatColor.RED).append("\u2716 No Access");
                }
            }

            sender.sendMessage(row.toString());
        }

        // Footer
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&8&m                                                  "));
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/" + label + " list <page>"
                    + ChatColor.GRAY + " to view other pages.");
        }
    }

    private void sendWarpInfo(CommandSender sender, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " info <name>");
            return;
        }

        WarpPoint warp = store.get(args[1]).orElse(null);
        if (warp == null) {
            sender.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist.");
            return;
        }

        Location location = warp.getLocation();
        String privacyStatus = warp.isPrivate()
                ? (ChatColor.RED + "\u2588 Private")
                : (ChatColor.GREEN + "\u2588 Public");

        sender.sendMessage(ChatColor.GOLD + settings.titleSingular + ": " + ChatColor.AQUA + warp.getName());
        sender.sendMessage(ChatColor.GRAY + settings.ownerLabel + ": " + ChatColor.WHITE + warp.getOwnerName());
        sender.sendMessage(ChatColor.GRAY + "World: " + ChatColor.WHITE + location.getWorld().getName());
        sender.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.WHITE
                + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
        sender.sendMessage(ChatColor.GRAY + "Visits: " + ChatColor.WHITE + warp.getVisits());
        sender.sendMessage(ChatColor.GRAY + "Access: " + privacyStatus);
        if (warp.isPrivate() && canManageWarp(sender, warp)) {
            int allowedCount = warp.getAllowedPlayers().size();
            sender.sendMessage(ChatColor.GRAY + "Allowed players: " + ChatColor.WHITE + allowedCount);
        }
    }

    private void teleportUsingGo(CommandSender sender, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " go <name>");
            return;
        }

        teleportToWarp(sender, args[1]);
    }

    private void teleportToWarp(CommandSender sender, String warpName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can teleport to " + settings.warpLabelPlural + ".");
            return;
        }

        WarpPoint warp = store.get(warpName).orElse(null);
        if (warp == null) {
            player.sendMessage(ChatColor.RED + "That " + settings.warpLabel + " does not exist. Use /"
                    + settings.primaryCommand + " list to see available warps.");
            return;
        }

        // Access check — admins bypass private restrictions
        if (warp.isPrivate() && !warp.canAccess(player.getUniqueId())
                && !player.hasPermission(settings.adminPermission)) {
            player.sendMessage(ChatColor.RED + "\uD83D\uDD12 That " + settings.warpLabel
                    + " is private. You do not have access.");
            return;
        }

        Location location = warp.getLocation();
        if (location.getWorld() == null) {
            player.sendMessage(ChatColor.RED + "That warp's world is not loaded.");
            return;
        }

        player.teleport(location);
        warp.addVisit();
        store.save();
        player.sendMessage(ChatColor.GREEN + "Teleported to " + settings.warpLabel + " "
                + ChatColor.AQUA + warp.getName() + ChatColor.GREEN + ".");
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + settings.title);
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " go <name>" + ChatColor.GRAY + " - Teleport to a warp");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " list [page]" + ChatColor.GRAY + " - List warps");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " info <name>" + ChatColor.GRAY + " - Show warp details");
        if (sender.hasPermission(settings.createPermission)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " create <name>" + ChatColor.GRAY + " - Create a warp");
        }
        if (sender.hasPermission(settings.removePermission)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " delete <name>" + ChatColor.GRAY + " - Remove a warp");
        }
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " private <name>" + ChatColor.GRAY + " - Toggle warp privacy");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " allow <warp> <player>" + ChatColor.GRAY + " - Grant warp access");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " deny <warp> <player>" + ChatColor.GRAY + " - Revoke warp access");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " allowed <warp>" + ChatColor.GRAY + " - List allowed players");
    }

    private boolean isValidWarpName(String name) {
        return name.length() >= MIN_NAME_LENGTH
                && name.length() <= MAX_NAME_LENGTH
                && name.matches("[A-Za-z0-9_-]+");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(settings.basePermission)) {
            return Collections.emptyList();
        }

        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.addAll(Arrays.asList("help", "list", "info", "go", "private", "allow", "deny", "allowed"));
            if (sender.hasPermission(settings.createPermission)) {
                options.add("create");
            }
            if (sender.hasPermission(settings.removePermission)) {
                options.add("delete");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            int pageSize = Math.max(1, settings.listPageSize);
            int totalPages = (int) Math.ceil((double) store.all().size() / pageSize);
            int maxPage = Math.min(totalPages, 10);
            for (int i = 1; i <= maxPage; i++) {
                options.add(String.valueOf(i));
            }
        } else if (args.length == 2 && isWarpNameSubCommand(args[0])) {
            // For go/info: only show accessible warps (hides private warps from tab-complete)
            if (sender instanceof Player player
                    && (args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("info"))) {
                options.addAll(store.accessibleNames(player.getUniqueId()));
            } else {
                // For delete/private/allow/deny/allowed: show all warps (owner/admin needs to see them)
                options.addAll(store.names());
            }
        } else if (args.length == 3
                && (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("deny"))) {
            // Tab-complete online player names for allow/deny
            for (Player online : Bukkit.getOnlinePlayers()) {
                options.add(online.getName());
            }
        }

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], options, completions);
        Collections.sort(completions);
        return completions;
    }

    private boolean isWarpNameSubCommand(String subCommand) {
        return subCommand.equalsIgnoreCase("delete")
                || subCommand.equalsIgnoreCase("info")
                || subCommand.equalsIgnoreCase("go")
                || subCommand.equalsIgnoreCase("private")
                || subCommand.equalsIgnoreCase("allow")
                || subCommand.equalsIgnoreCase("deny")
                || subCommand.equalsIgnoreCase("allowed");
    }




    public static class Settings {
        private final String primaryCommand;
        private final String title;
        private final String titleSingular;
        private final String warpLabel;
        private final String warpLabelPlural;
        private final String ownerLabel;
        private final String basePermission;
        private final String createPermission;
        private final String removePermission;
        private final String adminPermission;
        private final boolean ownerCanRemove;
        private final boolean showOwnerInList;
        private final int listPageSize;

        public Settings(
                String primaryCommand,
                String title,
                String titleSingular,
                String warpLabel,
                String warpLabelPlural,
                String ownerLabel,
                String basePermission,
                String createPermission,
                String removePermission,
                String adminPermission,
                boolean ownerCanRemove,
                boolean showOwnerInList,
                int listPageSize
        ) {
            this.primaryCommand = primaryCommand;
            this.title = title;
            this.titleSingular = titleSingular;
            this.warpLabel = warpLabel;
            this.warpLabelPlural = warpLabelPlural;
            this.ownerLabel = ownerLabel;
            this.basePermission = basePermission;
            this.createPermission = createPermission;
            this.removePermission = removePermission;
            this.adminPermission = adminPermission;
            this.ownerCanRemove = ownerCanRemove;
            this.showOwnerInList = showOwnerInList;
            this.listPageSize = listPageSize;
        }
    }
}
