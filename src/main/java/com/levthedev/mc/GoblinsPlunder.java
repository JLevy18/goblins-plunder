package com.levthedev.mc;

import org.bukkit.plugin.java.JavaPlugin;

import com.levthedev.mc.commands.CommandBase;
import com.levthedev.mc.managers.DatabaseManager;

public final class GoblinsPlunder extends JavaPlugin {


    @Override
    public void onEnable(){
        
        // Initialize all the things
        DatabaseManager.getInstance(this).getDatabaseCoordinator().setup();
        registerCommands();
    }

    @Override
    public void onDisable(){
        DatabaseManager.getInstance(this).closePool();
    }



    // Register commands for plugin
    private void registerCommands(){
        this.getCommand("gp").setExecutor(new CommandBase());
    }
    
}
