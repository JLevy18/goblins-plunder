package com.levthedev.mc.commands.subcommands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.listeners.AddListener;
import com.levthedev.mc.listeners.OpenPlunderListener;
import com.levthedev.mc.managers.ConfigManager;
import com.levthedev.mc.managers.ListenerManager;
import com.levthedev.mc.utility.LootTablesOverworld;

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
        return "/gp add <LootTable>";
    }

    @Override
    public void execute(Player player, String[] args) {

        AddListener addListener = (AddListener) ListenerManager.getInstance().getListeners().get("add");

        if (args.length == 1) {
            addListener.setActive(player, true);
            player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
        } else if (args.length == 2) {

            try { 
                addListener.setLoot(player, LootTablesOverworld.valueOf(args[1]));
                addListener.setActive(player, true);
                player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
            } catch (IllegalArgumentException e) {
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Invalid LootTable");
            }
            
        }

    }


    
}