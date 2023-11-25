package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.listeners.AddListener;

import net.md_5.bungee.api.ChatColor;

public class AddCommand extends SubCommand{

    private AddListener addListener;

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
    public void execute(Player player, String[] args, Map<String,Listener> listeners) {

        addListener = (AddListener) listeners.get(this.getName());
        addListener.setActive(true);

        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Click a block to create new Plunder");
    }


    
}
