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

        return ChatColor.translateAlternateColorCodes('&', config.getString("messages.plunder.title", "&2Plunder"));

    }

    public String getPrefix(){
        return ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "[GP] " + ChatColor.RESET + "";
    }

    public String getErrorPrefix(){
        return ChatColor.BOLD + "" + ChatColor.DARK_RED + "[GP Error] " + ChatColor.RESET + "";
    }


}
