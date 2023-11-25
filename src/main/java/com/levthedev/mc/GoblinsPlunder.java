package com.levthedev.mc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.levthedev.mc.listeners.AddListener;
import com.levthedev.mc.listeners.GetListener;
import com.levthedev.mc.managers.CommandManager;
import com.levthedev.mc.managers.DatabaseManager;

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
        listeners.put("get", new GetListener());
    }


    @Override
    public void onEnable(){
        instance = this;
        // Initialize all the things
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
    }

    private void registerListeners() {
        for (Map.Entry<String,Listener> entry : listeners.entrySet()) {
            getServer().getPluginManager().registerEvents(entry.getValue(), instance);
        }

    }
    
}
