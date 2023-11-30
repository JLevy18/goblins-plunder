package com.levthedev.mc.managers;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levthedev.mc.commands.SubCommand;
import com.levthedev.mc.commands.subcommands.AddCommand;
import com.levthedev.mc.commands.subcommands.HelpCommand;
import com.levthedev.mc.commands.subcommands.ReloadCommand;
import com.levthedev.mc.commands.subcommands.RestockCommand;

public class CommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> commands = new ArrayList<>();

    public CommandManager(){
        
        commands.add(new ReloadCommand());
        commands.add(new HelpCommand());
        commands.add(new AddCommand());
        commands.add(new RestockCommand());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Base command passed
            if (args.length == 0) {
                HelpCommand help = new HelpCommand();
                help.execute(player, args);
                return true;
            }
            

            // Sub command passed
            if (args.length > 0) {

                for (SubCommand subcommand : commands){
                    if (args[0].equalsIgnoreCase(subcommand.getName())){
                        subcommand.execute(player, args);
                        return true;
                    }
                }

            }



        }

        return true;
    }
    
}
