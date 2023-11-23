package com.levthedev.mc;

import org.bukkit.plugin.java.JavaPlugin;

import com.levthedev.mc.listeners.AddPlunderListener;
import com.levthedev.mc.managers.CommandManager;
import com.levthedev.mc.managers.DatabaseManager;

public final class GoblinsPlunder extends JavaPlugin {


    AddPlunderListener addPlunderListener = new AddPlunderListener();


    @Override
    public void onEnable(){
        
        // Initialize all the things
        DatabaseManager.initialize(this);
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable(){
        DatabaseManager.getInstance().closePool();
    }



    // Register commands for plugin
    private void registerCommands() {
        this.getCommand("gp").setExecutor(new CommandManager(addPlunderListener));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(addPlunderListener, this);
    }
    
}
