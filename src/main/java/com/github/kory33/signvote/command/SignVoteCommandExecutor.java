package com.github.kory33.signvote.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

public class SignVoteCommandExecutor implements CommandExecutor{
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;
    
    public SignVoteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    private void displayCreateCommandHelp(CommandSender sender) {
        sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.CREATE_COMMAND_HELP));
    }
    
    public boolean onCreateCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.CREATE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        try {
            String voteSessionName = args.remove(0);
            this.voteSessionManager.addSession(new VoteSession(voteSessionName));
        } catch (Exception exception) {
            this.displayCreateCommandHelp(sender);
        }
        
        return true;
    }
    
    public boolean onHelpCommand(CommandSender sender) {
        sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.COMMAND_HELP));
        return true;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

        String subCommand = argList.remove(0);
        switch(subCommand) {
            case "create":
                return this.onCreateCommand(sender, command, argList);
        }

        return this.onHelpCommand(sender);
    }
}
