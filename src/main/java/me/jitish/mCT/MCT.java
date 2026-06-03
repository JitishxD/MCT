package me.jitish.mCT;

import me.jitish.mCT.commands.*;
import me.jitish.mCT.listeners.AfkListener;
import me.jitish.mCT.listeners.ColorCodesDemo;
import me.jitish.mCT.listeners.SpawnEvents;
import me.jitish.mCT.listeners.PingDisplayListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCT extends JavaPlugin {

    private static MCT pluginInstanceVarWithMethod1;
    private AfkListener afkListener;

    //Getter for pluginInstanceVarWithMethod1
    public static MCT getPluginInstanceVar() {
        return pluginInstanceVarWithMethod1;
    }

    @Override
    public void onEnable() {

        pluginInstanceVarWithMethod1 = this;

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

        // Register event listeners
        getServer().getPluginManager().registerEvents(dieCommand, this);
        getServer().getPluginManager().registerEvents(new ColorCodesDemo(), this);
        getServer().getPluginManager().registerEvents(new SpawnEvents(this), this);
        getServer().getPluginManager().registerEvents(pingDisplayListener, this);
        getServer().getPluginManager().registerEvents(afkListener, this);

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

        // Start the AFK idle-checker scheduler
        afkListener.startIdleChecker();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (afkListener != null) {
            afkListener.stopIdleChecker();
        }
    }
}
