package com.levthedev.mc.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.managers.DatabaseManager;

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



        DatabaseManager.getInstance().resetPlunderStateTableAsync();

        Bukkit.broadcastMessage( ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GOLD +"\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592"  + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################" + "\n" + ChatColor.RESET +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW +"\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592"  + "\n" +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_GREEN + "   " + ChatColor.BOLD + "         " + ChatColor.UNDERLINE + "Goblin's Plunder" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + "\n" +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "              " + ChatColor.BOLD +  "All loot in " + ChatColor.LIGHT_PURPLE + "<World>" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "           " + ChatColor.BOLD + "has been restocked!" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_PURPLE + "            \u273D Happy Plundering \u273D" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################");
    }
    
}
