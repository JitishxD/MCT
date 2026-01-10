package me.jitish.mCT;

import me.jitish.mCT.commands.*;
import me.jitish.mCT.listeners.ColorCodesDemo;
import me.jitish.mCT.listeners.SpawnEvents;
import me.jitish.mCT.listeners.PingDisplayListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCT extends JavaPlugin {

    private static MCT pluginInstanceVarWithMethod1;

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

        PingDisplayListener pingDisplayListener = new PingDisplayListener();
        Ping pingCommand = new Ping(pingDisplayListener);

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Die(), this);
        getServer().getPluginManager().registerEvents(new ColorCodesDemo(), this);
        getServer().getPluginManager().registerEvents(new SpawnEvents(this), this);
        getServer().getPluginManager().registerEvents(pingDisplayListener, this);


        //All commands are as follows
        getCommand("die").setExecutor(new Die());
        getCommand("god").setExecutor(new God());
        getCommand("FeedMe").setExecutor(new FeedMe());
        getCommand("HealMe").setExecutor(new HealMe());
        getCommand("setHealth").setExecutor(new SetHealth());
        getCommand("setSpawn").setExecutor(new SetSpawn(this));
        getCommand("spawn").setExecutor(new Spawn(this));
        getCommand("fly").setExecutor(new Fly());
        getCommand("nightVision").setExecutor(new NightVision());
        getCommand("ping").setExecutor(pingCommand);

        //Tab completer for some commands
        getCommand("FeedMe").setTabCompleter(new FeedMe());
        getCommand("HealMe").setTabCompleter(new HealMe());
        getCommand("nightVision").setTabCompleter(new NightVision());
        getCommand("setHealth").setTabCompleter(new SetHealth());
        getCommand("fly").setTabCompleter(new Fly());
        getCommand("god").setTabCompleter(new God());
        getCommand("ping").setTabCompleter(pingCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
