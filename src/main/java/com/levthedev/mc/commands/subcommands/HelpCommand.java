package com.levthedev.mc.commands.subcommands;

import org.bukkit.command.CommandSender;

import com.levthedev.mc.commands.SubCommand;

import net.md_5.bungee.api.ChatColor;

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
    public void execute( CommandSender sender, String[] args) {
        sender.sendMessage(
            
       ChatColor.GOLD + "" + ChatColor.BOLD + "===========[" + ChatColor.DARK_GREEN + " Goblins Plunder" + ChatColor.YELLOW + "" + ChatColor.BOLD + " Help" + ChatColor.GOLD + "" + ChatColor.BOLD + "]============\n" + ChatColor.RESET + "" +
       ChatColor.DARK_GREEN + "/gp help " + ChatColor.GREEN + ": Display a list of all commands.\n" +
       ChatColor.DARK_GREEN +"/gp reload " + ChatColor.GREEN + ": Reload the config file.\n" +
       ChatColor.DARK_GREEN +"/gp add [flags]" + ChatColor.GREEN + ": Add a plunder container to the database.\n" +
                             "  -ignoreRestock : " + ChatColor.GRAY + "This chest will no longer be restocked.\n" +
       ChatColor.DARK_GREEN +"/gp restock <World> " + ChatColor.GREEN + ": Restock the plunders in a world.\n" +
       ChatColor.DARK_GREEN +"/gp remove <World> " + ChatColor.GREEN + ": Remove the plunders in a world.\n" +
        
       ChatColor.GOLD + "" + ChatColor.BOLD + "=========================================\n"
        
        );

    }
    
}
