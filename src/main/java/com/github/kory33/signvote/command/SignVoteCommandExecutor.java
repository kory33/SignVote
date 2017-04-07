package com.github.kory33.signvote.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.core.SignVote;

public class SignVoteCommandExecutor implements CommandExecutor{
    private final SignVote plugin;
    
    public SignVoteCommandExecutor(SignVote plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
