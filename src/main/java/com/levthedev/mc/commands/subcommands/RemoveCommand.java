package com.levthedev.mc.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.DatabaseManager;

public class RemoveCommand extends SubCommand{

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() { 
        return "Manually removes a plunder";
    }

    @Override
    public String getUsage() {
        return "/gp remove <World>";
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length == 2) {
            if (Bukkit.getWorld(args[1]) == null){
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + args[1] + " does not exist.");
                return;
            }

            DatabaseManager.getInstance().deletePlunderBlocksByWorldAsync(args[1], player);

        } else {
            player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Invalid arguments.");
        }

    }


    
}