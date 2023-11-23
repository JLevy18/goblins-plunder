package com.levthedev.mc.subcommands;

import org.bukkit.entity.Player;

import com.levthedev.mc.listeners.AddPlunderListener;

import net.md_5.bungee.api.ChatColor;

public class AddCommand extends SubCommand{

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a plunder block to the database.";
    }

    @Override
    public String getUsage() {
        return "/gp add";
    }

    @Override
    public void execute(Player player, String[] args, AddPlunderListener addPlunderListener) {
        addPlunderListener.setActive(true);
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Click a block to create new Plunder");
    }
    
}
