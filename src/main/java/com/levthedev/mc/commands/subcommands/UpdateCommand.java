package com.levthedev.mc.commands.subcommands;

import org.bukkit.command.CommandSender;

import com.levthedev.mc.commands.SubCommand;

public class UpdateCommand extends SubCommand {
    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() { 
        return "Update a plunder";
    }

    @Override
    public String getUsage() {
        return "/gp update [flags]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


    }
}
