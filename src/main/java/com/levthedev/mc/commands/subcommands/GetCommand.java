package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.listeners.GetListener;


public class GetCommand extends SubCommand{

    private GetListener getListener;

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "Fills chest";
    }

    @Override
    public String getUsage() {
        return "/gp get";
    }

    @Override
    public void execute(Player player, String[] args, Map<String,Listener> listeners) {
        getListener = (GetListener) listeners.get(this.getName());
        getListener.setActive(true);
       
    }
    
}
