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
    private final List<String> restockOptions = new ArrayList<>();
    private final List<String> addOptions = new ArrayList<>();
    private final List<String> removeOptions = new ArrayList<>();


    public TabCompleteManager() {
        
        commands.add("reload");
        commands.add("help");
        commands.add("add");
        commands.add("restock");
        commands.add("remove");



        addOptions.add("-ignoreRestock");

        restockOptions.add(ChatColor.ITALIC + "<World>");
        restockOptions.add("all");
        
        removeOptions.add(ChatColor.ITALIC + "<World>");

    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            StringUtil.copyPartialMatches(args[1], addOptions, completions);
        }


        if (args.length == 2 && args[0].equalsIgnoreCase("restock")) {
            StringUtil.copyPartialMatches(args[1], restockOptions, completions);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            StringUtil.copyPartialMatches(args[1], removeOptions, completions);
        }

        return completions;
    }
    
}
