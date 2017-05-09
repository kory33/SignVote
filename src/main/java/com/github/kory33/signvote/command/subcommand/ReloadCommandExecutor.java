package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;

/**
 * Executor class of "reload" sub-command
 * @author Kory
 */
public class ReloadCommandExecutor implements SubCommandExecutor {
    private final SignVote plugin;
    private final JSONConfiguration messageConfiguration;
    
    public ReloadCommandExecutor(SignVote plugin) {
        this.plugin = plugin;
        this.messageConfiguration = plugin.getMessagesConfiguration();
    }
    
    @Override
    public String getHelpString() {
        return "";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.RELOAD)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }
        
        this.plugin.reload();
        sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.RELOAD_COMPLETE));
        return true;
    }
}
