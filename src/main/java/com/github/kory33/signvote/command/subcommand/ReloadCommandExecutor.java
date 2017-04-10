package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;

public class ReloadCommandExecutor extends SubCommandExecutor {
    private final SignVote plugin;
    private final JSONConfiguration messageConfiguration;
    
    public ReloadCommandExecutor(SignVote plugin) {
        this.plugin = plugin;
        this.messageConfiguration = plugin.getMessagesConfiguration();
    }
    
    @Override
    protected String getHelpString() {
        return "";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.RELOAD)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        this.plugin.reload();
        sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.RELOAD_COMPLETE));
        return true;
    }
}
