package com.levthedev.mc.commands.subcommands;

import org.bukkit.entity.Player;

import com.levthedev.mc.GoblinsPlunder;
import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.managers.ConfigManager;

import net.md_5.bungee.api.ChatColor;

public class ReloadCommand extends SubCommand{

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() { 
        return "Reload the config";
    }

    @Override
    public String getUsage() {
        return "/gp reload";
    }

    @Override
    public void execute(Player player, String[] args) {
        ConfigManager.getInstance().reloadConfig();
        player.sendMessage(ChatColor.GREEN + "[GP] Config reloaded!");
    }
    
}
