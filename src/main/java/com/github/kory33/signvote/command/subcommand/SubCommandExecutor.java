package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SubCommandExecutor {
    protected abstract String getHelpString();
    
    public abstract boolean onCommand(CommandSender sender, Command command, ArrayList<String> args);

    public void displayHelp(CommandSender target) {
        target.sendMessage(this.getHelpString());
    }
}
