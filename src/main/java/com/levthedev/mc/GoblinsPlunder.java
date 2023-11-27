package com.levthedev.mc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.levthedev.mc.listeners.AddListener;
import com.levthedev.mc.listeners.PlunderListener;
import com.levthedev.mc.managers.CommandManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.TabCompleteManager;

public final class GoblinsPlunder extends JavaPlugin {



    private static GoblinsPlunder instance;

    Map<String,Listener> listeners = new HashMap<String,Listener>();

    public static synchronized GoblinsPlunder getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GoblinsPlunder not initialized");
        }

        return instance;
    }

    public GoblinsPlunder() {
        listeners.put("add", new AddListener());
        listeners.put("", new PlunderListener());
    }


    @Override
    public void onEnable(){

        // Initialize all the things
        instance = this;
        saveDefaultConfig();
        DatabaseManager.initialize();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable(){
        DatabaseManager.getInstance().closePool();
    }

    // Register commands for plugin
    private void registerCommands() {
        this.getCommand("gp").setExecutor(new CommandManager(listeners));
        this.getCommand("gp").setTabCompleter(new TabCompleteManager());
    }

    private void registerListeners() {
        for (Map.Entry<String,Listener> entry : listeners.entrySet()) {
            getServer().getPluginManager().registerEvents(entry.getValue(), instance);
        }

    }
    
}
