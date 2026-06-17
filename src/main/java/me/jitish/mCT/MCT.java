package me.jitish.mCT;

import me.jitish.mCT.tools.commands.*;
import me.jitish.mCT.tools.listeners.AfkListener;
import me.jitish.mCT.tools.listeners.ColorCodesDemo;
import me.jitish.mCT.tools.listeners.PingDisplayListener;
import me.jitish.mCT.tools.spawn.SetSpawn;
import me.jitish.mCT.tools.spawn.Spawn;
import me.jitish.mCT.tools.spawn.SpawnEvents;
import me.jitish.mCT.tpa.TpaListener;
import me.jitish.mCT.tpa.TpaManager;
import me.jitish.mCT.tpa.TpaSettings;
import me.jitish.mCT.tpa.TpaStorage;
import me.jitish.mCT.tpa.commands.*;
import me.jitish.mCT.tools.compatibility.LegacyFeaturesHandler;
import me.jitish.mCT.tools.compatibility.ModernFeaturesHandler;
import me.jitish.mCT.tools.compatibility.VersionHandler;
import me.jitish.mCT.warps.WarpCommand;
import me.jitish.mCT.warps.WarpStore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class MCT extends JavaPlugin {

    private static MCT pluginInstanceVarWithMethod1;
    private AfkListener afkListener;
    private WarpStore playerWarpStore;
    private WarpStore serverWarpStore;
    private TpaStorage tpaStorage;
    public VersionHandler versionHandler;

    //Getter for pluginInstanceVarWithMethod1
    public static MCT getPluginInstanceVar() {
        return pluginInstanceVarWithMethod1;
    }

    @Override
    public void onEnable() {

        pluginInstanceVarWithMethod1 = this;

        if (!setupVersionHandler()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Configuration file setup (config.yml)
        getConfig().options().copyDefaults(); // todo this line of code is suspicious
        saveDefaultConfig();

        // Create single instances for commands that also serve as TabCompleters/Listeners
        Die dieCommand = new Die();
        God godCommand = new God();
        FeedMe feedMeCommand = new FeedMe();
        HealMe healMeCommand = new HealMe();
        SetHealth setHealthCommand = new SetHealth();
        SetFood setFoodCommand = new SetFood();
        Fly flyCommand = new Fly();
        NightVision nightVisionCommand = new NightVision();
        Repair repairCommand = new Repair();
        Enchantt enchanttCommand = new Enchantt();
        Denchant denchantCommand = new Denchant();
        Summonn summonCommand = new Summonn();

        PingDisplayListener pingDisplayListener = new PingDisplayListener();
        Ping pingCommand = new Ping(pingDisplayListener);

        afkListener = new AfkListener();
        Afk afkCommand = new Afk(afkListener);

        SetSpawn setSpawnCommand = new SetSpawn(this);
        Spawn spawnCommand = new Spawn(this);
        playerWarpStore = new WarpStore(this, "player-warps.warps", "player warp");
        serverWarpStore = new WarpStore(this, "server-warps.warps", "server warp");
        WarpCommand playerWarpsCommand = new WarpCommand(playerWarpStore, new WarpCommand.Settings(
                "pwarp",
                "Player warps",
                "Player Warp",
                "player warp",
                "player warps",
                "Owner",
                "MCT.pwarp",
                "MCT.pwarp.create",
                "MCT.pwarp.remove",
                "MCT.pwarp.admin",
                true,
                true,
                8
        ));
        WarpCommand serverWarpsCommand = new WarpCommand(serverWarpStore, new WarpCommand.Settings(
                "swarp",
                "Server warps",
                "Server Warp",
                "server warp",
                "server warps",
                "Created by",
                "MCT.swarp",
                "MCT.swarp.admin",
                "MCT.swarp.admin",
                "MCT.swarp.admin",
                false,
                false,
                8
        ));

        // TPA system
        tpaStorage = new TpaStorage();
        TpaSettings tpaSettings = new TpaSettings(this);
        TpaManager tpaManager = new TpaManager(this, tpaStorage, tpaSettings);
        TpaListener tpaListener = new TpaListener(tpaManager, tpaStorage, tpaSettings);

        TpaCommand tpaCommand = new TpaCommand(tpaManager, tpaStorage, tpaSettings);
        TpaHereCommand tpaHereCommand = new TpaHereCommand(tpaManager, tpaStorage, tpaSettings);
        TpAcceptCommand tpAcceptCommand = new TpAcceptCommand(tpaManager);
        TpDenyCommand tpDenyCommand = new TpDenyCommand(tpaManager);
        TpCancelCommand tpCancelCommand = new TpCancelCommand(tpaManager);
        BackCommand backCommand = new BackCommand(tpaManager, tpaStorage, tpaSettings);
        TpaToggleCommand tpaToggleCommand = new TpaToggleCommand(tpaManager, tpaStorage);
        TpaIgnoreCommand tpaIgnoreCommand = new TpaIgnoreCommand(tpaManager, tpaStorage);
        TpautoCommand tpautoCommand = new TpautoCommand(tpaManager, tpaStorage);
        TpaHereAllCommand tpaHereAllCommand = new TpaHereAllCommand(tpaManager, tpaStorage, tpaSettings);

        // Register event listeners
        getServer().getPluginManager().registerEvents(dieCommand, this);
        getServer().getPluginManager().registerEvents(new ColorCodesDemo(), this);
        getServer().getPluginManager().registerEvents(new SpawnEvents(this), this);
        getServer().getPluginManager().registerEvents(pingDisplayListener, this);
        getServer().getPluginManager().registerEvents(afkListener, this);
        getServer().getPluginManager().registerEvents(tpaListener, this);

        // Register command executors
        getCommand("die").setExecutor(dieCommand);
        getCommand("god").setExecutor(godCommand);
        getCommand("FeedMe").setExecutor(feedMeCommand);
        getCommand("HealMe").setExecutor(healMeCommand);
        getCommand("setHealth").setExecutor(setHealthCommand);
        getCommand("setFood").setExecutor(setFoodCommand);
        getCommand("setSpawn").setExecutor(setSpawnCommand);
        getCommand("spawn").setExecutor(spawnCommand);
        getCommand("fly").setExecutor(flyCommand);
        getCommand("nightVision").setExecutor(nightVisionCommand);
        getCommand("ping").setExecutor(pingCommand);
        getCommand("repair").setExecutor(repairCommand);
        getCommand("afk").setExecutor(afkCommand);
        getCommand("enchantt").setExecutor(enchanttCommand);
        getCommand("denchant").setExecutor(denchantCommand);
        getCommand("summonn").setExecutor(summonCommand);
        getCommand("pwarp").setExecutor(playerWarpsCommand);
        getCommand("swarp").setExecutor(serverWarpsCommand);
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpahere").setExecutor(tpaHereCommand);
        getCommand("tpaccept").setExecutor(tpAcceptCommand);
        getCommand("tpdeny").setExecutor(tpDenyCommand);
        getCommand("tpcancel").setExecutor(tpCancelCommand);
        getCommand("back").setExecutor(backCommand);
        getCommand("tpatoggle").setExecutor(tpaToggleCommand);
        getCommand("tpaignore").setExecutor(tpaIgnoreCommand);
        getCommand("tpauto").setExecutor(tpautoCommand);
        getCommand("tpahereall").setExecutor(tpaHereAllCommand);

        // Register tab completers (same instances — they implement both interfaces)
        getCommand("god").setTabCompleter(godCommand);
        getCommand("FeedMe").setTabCompleter(feedMeCommand);
        getCommand("HealMe").setTabCompleter(healMeCommand);
        getCommand("setHealth").setTabCompleter(setHealthCommand);
        getCommand("fly").setTabCompleter(flyCommand);
        getCommand("nightVision").setTabCompleter(nightVisionCommand);
        getCommand("ping").setTabCompleter(pingCommand);
        getCommand("repair").setTabCompleter(repairCommand);
        getCommand("afk").setTabCompleter(afkCommand);
        getCommand("setFood").setTabCompleter(setFoodCommand);
        getCommand("enchantt").setTabCompleter(enchanttCommand);
        getCommand("denchant").setTabCompleter(denchantCommand);
        getCommand("summonn").setTabCompleter(summonCommand);
        getCommand("pwarp").setTabCompleter(playerWarpsCommand);
        getCommand("swarp").setTabCompleter(serverWarpsCommand);
        getCommand("tpa").setTabCompleter(tpaCommand);
        getCommand("tpahere").setTabCompleter(tpaHereCommand);
        getCommand("tpaccept").setTabCompleter(tpAcceptCommand);
        getCommand("tpdeny").setTabCompleter(tpDenyCommand);
        getCommand("tpcancel").setTabCompleter(tpCancelCommand);
        getCommand("back").setTabCompleter(backCommand);
        getCommand("tpatoggle").setTabCompleter(tpaToggleCommand);
        getCommand("tpaignore").setTabCompleter(tpaIgnoreCommand);
        getCommand("tpauto").setTabCompleter(tpautoCommand);
        getCommand("tpahereall").setTabCompleter(tpaHereAllCommand);

        // Start the AFK idle-checker scheduler
        afkListener.startIdleChecker();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (afkListener != null) {
            afkListener.stopIdleChecker();
        }
        if (playerWarpStore != null) {
            playerWarpStore.save();
        }
        if (serverWarpStore != null) {
            serverWarpStore.save();
        }
        if (tpaStorage != null) {
            tpaStorage.clearAll();
        }
    }

    private boolean setupVersionHandler() {
        String[] packageParts = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if (packageParts.length > 3) {
            String version = packageParts[3];
            int minorVersion = parseMinorVersion(version);
            if (minorVersion > 0 && minorVersion < 8) {
                getLogger().severe("Unsupported Minecraft server version: " + version + ". MCT requires Minecraft 1.8.8 or newer.");
                return false;
            }

            if (minorVersion >= 13 || minorVersion == -1) {
                versionHandler = new ModernFeaturesHandler();
                getLogger().info("Loaded ModernFeaturesHandler for " + version);
            } else {
                versionHandler = new LegacyFeaturesHandler();
                getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) versionHandler, this);
                getLogger().info("Loaded LegacyFeaturesHandler for " + version);
            }
        } else {
            // Modern Paper (1.20.5+) removed the NMS version string from the package name.
            // Since this is latest it fully supports the modern API.
            versionHandler = new ModernFeaturesHandler();
            getLogger().info("Loaded ModernFeaturesHandler for non-versioned package: " + String.join(".", packageParts));
        }
        return true;
    }

    private int parseMinorVersion(String version) {
        if (version == null || !version.startsWith("v1_")) {
            return -1;
        }

        String remainder = version.substring(3);
        int separator = remainder.indexOf('_');
        String minor = separator >= 0 ? remainder.substring(0, separator) : remainder;
        try {
            return Integer.parseInt(minor);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
