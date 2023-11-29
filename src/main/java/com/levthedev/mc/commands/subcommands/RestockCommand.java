package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;

public class RestockCommand extends SubCommand{

    @Override
    public String getName() {
        return "restock";
    }

    @Override
    public String getDescription() {
        return "Restock plunder";
    }

    @Override
    public String getUsage() {
        return "/gp restock";
    }

    @Override
    public void execute(Player player, String[] args) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}
