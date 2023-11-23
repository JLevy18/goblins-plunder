package com.levthedev.mc.subcommands;

import org.bukkit.entity.Player;

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
    public void execute(Player player, String[] args) {
        player.sendMessage("Trying to add");
    }
    
}
