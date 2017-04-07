package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.core.SignVote;

public class HelpCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    
    public HelpCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
    }
    
    @Override
    protected String getHelpString() {
        return messageConfiguration.getString(MessageConfigurationNodes.COMMAND_HELP);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        return false;
    }
    
}
