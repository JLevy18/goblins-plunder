package com.levthedev.mc.managers;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.levthedev.mc.GoblinsPlunder;

public class ConfigManager {


    private static final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static ConfigManager instance;
    private FileConfiguration config = plugin.getConfig();


    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigManager not initialized");
        }

        return instance;
    }

    private ConfigManager() {}

    public static synchronized void initialize() {
        instance = new ConfigManager();
    }

    public void reloadConfig(){
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public boolean isDebug(){
        return config.getBoolean("debug.enabled");
    }


    public String getPlunderTitle(){

        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.plunder.title", "&5Plunder"));

    }

    public String getPrefix(){
        return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[GP] " + ChatColor.RESET + "";
    }

    public String getErrorPrefix(){
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "[GP Error] " + ChatColor.RESET + "";
    }

    public boolean isPlunderInvincible(){
        return config.getBoolean("plunder.invincible");
    }

    // Generated Structures

    public boolean isGSEnabled(){
        return config.getBoolean("plunder.generated-structures.enabled");
    }

    public boolean isBroadcastRestockEnabled(){
        return config.getBoolean("messages.broadcast-restock.enabled");
    }

    public List<?> getGSWorldWhitelist(){
        return config.getList("plunder.generated-structures.world-whitelist");
    }

    public List<?> getGSBlacklist(){
        return config.getList("plunder.generated-structures.structure-blacklist");
    }
    
    


}
