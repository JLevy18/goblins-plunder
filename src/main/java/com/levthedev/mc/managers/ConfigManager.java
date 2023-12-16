package com.levthedev.mc.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.levthedev.mc.GoblinsPlunder;

public class ConfigManager {


    private static final GoblinsPlunder plugin = GoblinsPlunder.getInstance();
    private static ConfigManager instance;
    private static File logFolder = new File(plugin.getDataFolder(), "logs");

    private FileConfiguration config = plugin.getConfig();
    private Logger logger = plugin.getLogger();


    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigManager not initialized");
        }

        return instance;
    }

    private ConfigManager() {}

    public static synchronized void initialize() {
        instance = new ConfigManager();
        plugin.saveDefaultConfig();

        if (!logFolder.exists()){
            logFolder.mkdirs();
        }
    }

    public void logInventoryData(byte[] contents, String playerName, String blockId){

        File logFile = new File(logFolder, blockId + "_" + playerName);

        try (FileOutputStream fos = new FileOutputStream(logFile)) {
            fos.write(contents);
        } catch (IOException e) {
            logger.log(Level.SEVERE, this.getClass().getSimpleName() + ": Failed to save exact inventory log. An abbreviated contents can be found in the console logs.", e);
        }
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
