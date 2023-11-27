package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.listeners.AddListener;
import com.levthedev.mc.utility.LootTablesOverworld;

import net.md_5.bungee.api.ChatColor;

public class AddCommand extends SubCommand{

    private AddListener addListener;

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
        return "/gp add <LootTable>";
    }

    @Override
    public void execute(Player player, String[] args, Map<String,Listener> listeners) {

        addListener = (AddListener) listeners.get(this.getName());

        if (args.length == 1) {
            addListener.setActive(true);
            player.sendMessage(ChatColor.GREEN + "Click a container to convert to plunder");
        } else if (args.length == 2) {

            try { 
                addListener.setLoot(LootTablesOverworld.valueOf(args[1]));
                addListener.setActive(true);
                player.sendMessage(ChatColor.GREEN + "Click a container to convert to plunder");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ChatColor.DARK_RED + "" +  ChatColor.BOLD + "[GP Error] " + ChatColor.RED + "Invalid LootTable");
            }
            
        }

    }


    
}
