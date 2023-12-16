package com.levthedev.mc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.levthedev.mc.coordinators.DatabaseCoordinator;
import com.levthedev.mc.listeners.AddPlunderListener;
import com.levthedev.mc.listeners.ClosePlunderListener;
import com.levthedev.mc.listeners.OpenPlunderListener;
import com.levthedev.mc.listeners.RemovePlunderListener;
import com.levthedev.mc.managers.PlunderManager;
import com.levthedev.mc.managers.CommandManager;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;
import com.levthedev.mc.managers.ListenerManager;
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

    @Override
    public void onEnable(){

        // The order of these operations is very important in this function
        // Some of the managers are singletons and need to be initialized first

        // Initialize all the things
        instance = this;

        ConfigManager.initialize();

        DatabaseManager.initialize();
        DatabaseManager.getInstance().setDatabaseCoordinator(new DatabaseCoordinator());

        PlunderManager.initialize();

        populateListeners();
        ListenerManager.initialize(listeners);

        
        this.getCommand("gp").setExecutor(new CommandManager());
        this.getCommand("gp").setTabCompleter(new TabCompleteManager());

    }

    @Override
    public void onDisable(){
        DatabaseManager.getInstance().closePool();
    }

    // The order these are registered in is important because Bukkit API calls duplicate events synchronously in the order that they were registered
    private void populateListeners(){
        listeners.put("add", new AddPlunderListener());
        listeners.put("remove", new RemovePlunderListener());
        listeners.put("open", new OpenPlunderListener());
        listeners.put("close", new ClosePlunderListener());
    }
    
}
