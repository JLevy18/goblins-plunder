package com.levthedev.mc.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.levthedev.mc.listeners.AddPlunderListener;

public class HelpCommand extends SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "List of commands and their usages.";
    }

    @Override
    public String getUsage() {
        return "/gp help";
    }

    @Override
    public void execute(Player player, String[] args, AddPlunderListener addPlunderListener) {
        player.sendMessage(
            
        "==========[Goblins Plunder Help]===========\n" +
        "/gp help : Display a list of all commands.\n" +
        "/gp add : Add a plunder block to the database.\n" +
        
        "=========================================\n"
        
        );
    }
    
}
