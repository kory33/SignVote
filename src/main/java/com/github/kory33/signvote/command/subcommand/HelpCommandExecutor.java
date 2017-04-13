package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.core.SignVote;

public class HelpCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final Map<String, SubCommandExecutor> subCommandExecutorMap;
    
    public HelpCommandExecutor(SignVote plugin, Map<String, SubCommandExecutor> subCommandExecutorMap) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.subCommandExecutorMap = subCommandExecutorMap;
    }
    
    @Override
    protected String getHelpString() {
        return "";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        String messagePrefix = messageConfiguration.getString(MessageConfigurationNodes.MESSAGE_PREFIX);
        sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.COMMAND_HELP_HEADER));
        
        for (Entry<String, SubCommandExecutor> commandEntry: subCommandExecutorMap.entrySet()) {
            String commandHelp = messageConfiguration.getString("command." + commandEntry.getKey() + ".help");
            sender.sendMessage(messagePrefix + commandHelp);
        }
        
        return true;
    }
    
}
