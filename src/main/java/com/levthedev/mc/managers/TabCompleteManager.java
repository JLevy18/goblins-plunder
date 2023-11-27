package com.levthedev.mc.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import com.levthedev.mc.utility.LootTablesOverworld;

import net.md_5.bungee.api.ChatColor;

public class TabCompleteManager implements TabCompleter {

    private final List<String> commands = new ArrayList<>();
    private final List<String> addOptions = new ArrayList<>();
    private final List<String> refillOptions = new ArrayList<>();


    public TabCompleteManager() {
        commands.add("add");
        commands.add("help");
        commands.add("restock");

        for (LootTablesOverworld lootTable : LootTablesOverworld.values()) {
            addOptions.add(lootTable.name());
        }

        refillOptions.add(ChatColor.ITALIC + "<World>");

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
            StringUtil.copyPartialMatches(args[1], refillOptions, completions);
        }

        return completions;
    }
    
}
