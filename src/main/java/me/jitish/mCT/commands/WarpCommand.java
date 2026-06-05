package me.jitish.mCT.commands;

import me.jitish.mCT.warps.WarpPoint;
import me.jitish.mCT.warps.WarpStore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

public class WarpCommand implements CommandExecutor, TabCompleter {
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 24;
    private static final int NAME_COLUMN_MAX = 18;
    private static final int OWNER_COLUMN_MAX = 18;
    private static final int VISITS_COLUMN_MIN = 6;

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
            case "set":
            case "create":
                createWarp(sender, label, args);
                return true;
            case "remove":
            case "delete":
                removeWarp(sender, label, args);
                return true;
            case "list":
                listWarps(sender, label, args);
                return true;
            case "info":
                sendWarpInfo(sender, label, args);
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
            player.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " set <name>");
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
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " remove <name>");
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
        int indexWidth = Math.max(2, String.valueOf(warps.size()).length());

        int nameWidth = Math.max("Name".length(), 4);
        int ownerWidth = Math.max(settings.ownerLabel.length(), 5);
        int visitsWidth = Math.max("Visits".length(), VISITS_COLUMN_MIN);
        int maxVisits = 0;
        for (int i = startIndex; i < endIndex; i++) {
            WarpPoint warp = warps.get(i);
            nameWidth = Math.max(nameWidth, warp.getName().length());
            if (settings.showOwnerInList) {
                ownerWidth = Math.max(ownerWidth, warp.getOwnerName().length());
            }
            maxVisits = Math.max(maxVisits, warp.getVisits());
        }
        visitsWidth = Math.max(visitsWidth, String.valueOf(maxVisits).length());
        nameWidth = Math.min(NAME_COLUMN_MAX, nameWidth);
        ownerWidth = Math.min(OWNER_COLUMN_MAX, ownerWidth);

        int columns = settings.showOwnerInList ? 4 : 3;
        int separatorLength = indexWidth + nameWidth + visitsWidth + (settings.showOwnerInList ? ownerWidth : 0)
                + (columns - 1) * 3;
        String separator = ChatColor.DARK_GRAY + repeat("-", separatorLength);

        sender.sendMessage(ChatColor.GOLD + settings.title + ChatColor.GRAY + " (Page " + page + "/" + totalPages
                + ", " + warps.size() + " total)");
        sender.sendMessage(separator);
        sender.sendMessage(buildHeaderRow(indexWidth, nameWidth, ownerWidth, visitsWidth));
        sender.sendMessage(separator);

        for (int i = startIndex; i < endIndex; i++) {
            WarpPoint warp = warps.get(i);
            int displayIndex = i + 1;
            sender.sendMessage(buildDataRow(warp, displayIndex, indexWidth, nameWidth, ownerWidth, visitsWidth));
        }

        sender.sendMessage(separator);
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + "Use /" + label + " list <page> to view other pages.");
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
        sender.sendMessage(ChatColor.GOLD + settings.titleSingular + ": " + ChatColor.AQUA + warp.getName());
        sender.sendMessage(ChatColor.GRAY + settings.ownerLabel + ": " + ChatColor.WHITE + warp.getOwnerName());
        sender.sendMessage(ChatColor.GRAY + "World: " + ChatColor.WHITE + location.getWorld().getName());
        sender.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.WHITE
                + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
        sender.sendMessage(ChatColor.GRAY + "Visits: " + ChatColor.WHITE + warp.getVisits());
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
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " <name>" + ChatColor.GRAY + " - Teleport to a warp");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " list [page]" + ChatColor.GRAY + " - List warps");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " info <name>" + ChatColor.GRAY + " - Show warp details");
        if (sender.hasPermission(settings.createPermission)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " set <name>" + ChatColor.GRAY + " - Create a warp");
        }
        if (sender.hasPermission(settings.removePermission)) {
            sender.sendMessage(ChatColor.YELLOW + "/" + label + " remove <name>" + ChatColor.GRAY + " - Remove a warp");
        }
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
            options.addAll(Arrays.asList("help", "list", "info"));
            if (sender.hasPermission(settings.createPermission)) {
                options.add("set");
                options.add("create");
            }
            if (sender.hasPermission(settings.removePermission)) {
                options.add("remove");
                options.add("delete");
            }
            options.addAll(store.names());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            int pageSize = Math.max(1, settings.listPageSize);
            int totalPages = (int) Math.ceil((double) store.all().size() / pageSize);
            int maxPage = Math.min(totalPages, 10);
            for (int i = 1; i <= maxPage; i++) {
                options.add(String.valueOf(i));
            }
        } else if (args.length == 2 && isWarpNameSubCommand(args[0])) {
            options.addAll(store.names());
        }

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], options, completions);
        Collections.sort(completions);
        return completions;
    }

    private boolean isWarpNameSubCommand(String subCommand) {
        return subCommand.equalsIgnoreCase("remove")
                || subCommand.equalsIgnoreCase("delete")
                || subCommand.equalsIgnoreCase("info");
    }

    private String buildHeaderRow(int indexWidth, int nameWidth, int ownerWidth, int visitsWidth) {
        String row = ChatColor.GRAY + formatCell("#", indexWidth) + ChatColor.DARK_GRAY + " | "
                + ChatColor.GRAY + formatCell("Name", nameWidth);
        if (settings.showOwnerInList) {
            row += ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + formatCell(settings.ownerLabel, ownerWidth);
        }
        row += ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + formatCell("Visits", visitsWidth);
        return row;
    }

    private String buildDataRow(WarpPoint warp, int index, int indexWidth, int nameWidth, int ownerWidth, int visitsWidth) {
        String row = ChatColor.GRAY + formatCell(String.valueOf(index), indexWidth) + ChatColor.DARK_GRAY + " | "
                + ChatColor.AQUA + formatCell(warp.getName(), nameWidth);
        if (settings.showOwnerInList) {
            row += ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + formatCell(warp.getOwnerName(), ownerWidth);
        }
        row += ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + formatCell(String.valueOf(warp.getVisits()), visitsWidth);
        return row;
    }

    private String formatCell(String text, int width) {
        if (text.length() >= width) {
            if (width <= 3) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 3) + "...";
        }
        return text + repeat(" ", width - text.length());
    }

    private String repeat(String text, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(count * text.length());
        for (int i = 0; i < count; i++) {
            builder.append(text);
        }
        return builder.toString();
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
