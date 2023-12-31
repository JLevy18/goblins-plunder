package com.levthedev.mc.commands.subcommands;


import org.bukkit.command.CommandSender;
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
        return "/gp add [flags]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)){

            sender.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "The console cannot use this command!");
            return;
        }
        Player player = (Player) sender;

        AddPlunderListener addPlunderListener = (AddPlunderListener) ListenerManager.getInstance().getListeners().get("add");

        if (args.length == 1) {
            addPlunderListener.setActive(player, true, false);
            player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
        } else if (args.length > 1 && args[1].equalsIgnoreCase("-ignorerestock")){
            addPlunderListener.setActive(player, true, true);
            player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
        } else {
            player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Invalid arguments.");
        }

    }


    
}