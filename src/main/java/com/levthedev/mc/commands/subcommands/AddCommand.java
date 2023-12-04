package com.levthedev.mc.commands.subcommands;


import org.bukkit.entity.Player;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.listeners.AddPlunderListener;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.ListenerManager;

import net.md_5.bungee.api.ChatColor;

public class AddCommand extends SubCommand{

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() { 
        return "Manually adds a plunder";
    }

    @Override
    public String getUsage() {
        return "/gp add";
    }

    @Override
    public void execute(Player player, String[] args) {

        AddPlunderListener addPlunderListener = (AddPlunderListener) ListenerManager.getInstance().getListeners().get("add");

        if (args.length == 1) {
            addPlunderListener.setActive(player, true);
            player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
        } else if (args.length > 1){
            player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Invalid arguments.");
        }

    }


    
}