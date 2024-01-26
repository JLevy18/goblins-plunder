package com.levthedev.mc.commands.subcommands;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
    protected Map<String,String> processFlags(CommandSender sender, String[] args){
        
        Map<String,String> flags = new HashMap<>();


        for (int i = 0; i < args.length; i++){
            if (args[i].startsWith("-")){
                String flag = args[i].substring(1).toLowerCase();

                switch (flag) {
                    case "ignorerestock": {
                        flags.put(flag,null);
                        break;
                    }
                    default:
                        sender.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Unknown flag: " + flag);
                        return null;
                }


            }
        }

        return flags;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


       
        if (sender instanceof Player){
            Player player = (Player) sender;
            AddPlunderListener addPlunderListener = (AddPlunderListener) ListenerManager.getInstance().getListeners().get("add");
    
            if (args.length == 1) {
                addPlunderListener.setActivePlayer(player, true);
                player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
            } else if (args.length > 1){
                addPlunderListener.setActivePlayer(player, true);
                addPlunderListener.setPlayerFlags(player, processFlags(player, Arrays.copyOfRange(args, 2, args.length)));
                player.sendMessage(ConfigManager.getInstance().getPrefix() + ChatColor.GREEN + "Click a container to convert to plunder");
            } else {
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Invalid arguments.");
            }

        }

        // Location,blocktype,loottable is required
        else if (sender instanceof ConsoleCommandSender){
            sender.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "The console cannot use this command!");
            return;
        }
    }
}