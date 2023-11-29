package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;

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
    public void execute(Player player, String[] args) {
        player.sendMessage(
            
        "==========[Goblins Plunder Help]===========\n" +
        "/gp help : Display a list of all commands.\n" +
        "/gp add : Add a plunder block to the database.\n" +
        
        "=========================================\n"
        
        );
    }
    
}
