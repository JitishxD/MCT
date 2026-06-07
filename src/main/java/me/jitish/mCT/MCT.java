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

        setupVersionHandler();

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

    private void setupVersionHandler() {
        String pkg = getServer().getClass().getPackage().getName();
        String token = pkg.substring(pkg.lastIndexOf('.') + 1);

        if ("craftbukkit".equalsIgnoreCase(token)) {
            this.versionHandler = new ModernFeaturesHandler();
            return;
        }

        java.util.regex.Pattern p = java.util.regex.Pattern.compile("^v(\\d+)_(\\d+)_R(\\d+)$");
        java.util.regex.Matcher m = p.matcher(token);
        if (!m.matches()) {
            this.versionHandler = new ModernFeaturesHandler();
            return;
        }

        int major = Integer.parseInt(m.group(1));
        int minor = Integer.parseInt(m.group(2));

        if (major == 1 && minor < 13) {
            this.versionHandler = new LegacyFeaturesHandler();
            getServer().getPluginManager().registerEvents((org.bukkit.event.Listener) this.versionHandler, this);
        } else {
            this.versionHandler = new ModernFeaturesHandler();
        }
    }
}
