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

public class SignVoteCommandExecutor implements CommandExecutor{
    private final JSONConfiguration messageConfiguration;
    
    public SignVoteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
    }
    
    public boolean onCreateCommand(CommandSender sender, Command command, String[] args) {
        if (!sender.hasPermission(PermissionNodes.CREATE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        // TODO create session from command interpretations.
        
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
                return this.onCreateCommand(sender, command, args);
        }

        return this.onHelpCommand(sender);
    }
}
