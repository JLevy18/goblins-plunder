package com.levthedev.mc.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;

public class TabCompleteManager implements TabCompleter {

    private final List<String> commands = new ArrayList<>();
    private final List<String> refillOptions = new ArrayList<>();


    public TabCompleteManager() {
        
        commands.add("reload");
        commands.add("help");
        commands.add("add");
        commands.add("restock");

        refillOptions.add(ChatColor.ITALIC + "<World>");
        refillOptions.add("all");

    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("restock")) {
            StringUtil.copyPartialMatches(args[1], refillOptions, completions);
        }

        return completions;
    }
    
}
