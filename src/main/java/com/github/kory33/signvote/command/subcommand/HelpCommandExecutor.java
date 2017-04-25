package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.core.SignVote;

import net.md_5.bungee.api.ChatColor;

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
        String messagePrefix = messageConfiguration.getString(MessageConfigNodes.MESSAGE_PREFIX);
        sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.COMMAND_HELP_HEADER));
        
        for (Entry<String, SubCommandExecutor> commandEntry: subCommandExecutorMap.entrySet()) {
            String subcommandName = commandEntry.getKey();
            String commandHelp = messageConfiguration.getString("command." + subcommandName + ".summary");
            sender.sendMessage(messagePrefix + ChatColor.DARK_AQUA + subcommandName + ChatColor.GREEN + " - " + commandHelp);
        }
        
        return true;
    }
    
}
