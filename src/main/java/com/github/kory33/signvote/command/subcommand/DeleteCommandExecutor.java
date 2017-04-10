package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

public class DeleteCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public DeleteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigurationNodes.DELETE_SESS_COMMAND_HELP);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.DELETE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        if (args.size() < 1) {
            return false;
        }
        
        String targetSessionName = args.remove(0);
        VoteSession targetVoteSession = this.voteSessionManager.getVoteSession(targetSessionName);

        if (targetVoteSession == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        this.voteSessionManager.deleteSession(targetVoteSession);
        sender.sendMessage(this.messageConfiguration.getFormatted(MessageConfigurationNodes.F_SESSION_DELETED, targetSessionName));

        return true;
    }
    
}
