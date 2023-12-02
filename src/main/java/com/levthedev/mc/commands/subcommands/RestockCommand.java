package com.levthedev.mc.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.managers.ConfigManager;
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
        return "/gp restock <World>";
    }

    @Override
    public void execute(Player player, String[] args) {

        // Reset entire table

        if (args.length == 2 && args[1].equalsIgnoreCase("all")) { 
            DatabaseManager.getInstance().resetPlunderStateTableAsync();
            broadcast();
        }

        // Reset table by world name
        if (args.length == 2 && !args[1].equalsIgnoreCase("all")) {

            World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + args[1] + ChatColor.RED + " is not a valid world name.");
            } else {
                DatabaseManager.getInstance().deletePlunderStateByWorldAsync(args[1]);
                broadcast(args[1]);
            }
        }

        if (args.length != 2 ) {
            player.sendMessage(ConfigManager.getInstance().getErrorPrefix() + ChatColor.RED + "Please specify what to restock.");
        }
    }


    private void broadcast() {
        Bukkit.broadcastMessage( ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GOLD +"\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592"  + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################" + "\n" + ChatColor.RESET +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW +"\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592"  + "\n" +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_GREEN + "   " + ChatColor.BOLD + "         " + ChatColor.UNDERLINE + "Goblin's Plunder" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + "\n" +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "              " + ChatColor.BOLD +  "All loot in " + ChatColor.LIGHT_PURPLE + "Server" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "           " + ChatColor.BOLD + "has been restocked!" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_PURPLE + "            \u273D Happy Plundering \u273D" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################");
    }

    private void broadcast(String worldName) {
        Bukkit.broadcastMessage( ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588\u2588" + ChatColor.GOLD +"\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592"  + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################" + "\n" + ChatColor.RESET +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW +"\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592"  + "\n" +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_GREEN + "   " + ChatColor.BOLD + "         " + ChatColor.UNDERLINE + "Goblin's Plunder" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + "\n" +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "              " + ChatColor.BOLD +  "All loot in " + ChatColor.LIGHT_PURPLE + worldName + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588" + ChatColor.GOLD + "\u2588\u2592" + ChatColor.YELLOW + "\u2593\u2593\u2593" + ChatColor.GOLD + "\u2592\u2588\u2588"         + ChatColor.GREEN + "           " + ChatColor.BOLD + "has been restocked!" + "\n" + ChatColor.RESET +
                            ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592" + ChatColor.YELLOW + "\u2593" + ChatColor.GOLD + "\u2592\u2592\u2588\u2588"         + ChatColor.DARK_PURPLE + "            \u273D Happy Plundering \u273D" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2592\u2592\u2592\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592" + "\n" +
                            ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.YELLOW + "\u2588\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.DARK_GRAY + "\u2592\u2592" + ChatColor.GOLD + "   " + ChatColor.MAGIC + "##############################");
    }
    
}
