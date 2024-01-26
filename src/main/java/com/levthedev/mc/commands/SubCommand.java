package com.levthedev.mc.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();
    public abstract void execute(CommandSender sender, String args[]);

    protected Map<String,String> processFlags(CommandSender sender, String args[]){
        return null;
    }

}
