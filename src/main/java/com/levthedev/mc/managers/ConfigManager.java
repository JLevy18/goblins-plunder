package com.levthedev.mc.managers;

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
    


}
