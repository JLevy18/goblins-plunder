package com.levthedev.mc.commands;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class SubCommand {
    
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();
    public abstract void execute(Player player, String args[], Map<String, Listener> listeners);

}
