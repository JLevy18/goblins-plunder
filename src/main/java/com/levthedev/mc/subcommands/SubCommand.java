package com.levthedev.mc.subcommands;

import org.bukkit.entity.Player;

import com.levthedev.mc.listeners.AddPlunderListener;

public abstract class SubCommand {
    
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();
    public abstract void execute(Player player, String args[], AddPlunderListener plunderListener);

}
