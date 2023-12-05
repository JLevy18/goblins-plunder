package com.levthedev.mc.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();
    public abstract void execute(CommandSender sender, String args[]);

}
