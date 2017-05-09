package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

/**
 * Executor class of "delete" sub-command
 * @author Kory
 */
public class DeleteCommandExecutor implements SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public DeleteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    public String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigNodes.DELETE_SESS_COMMAND_HELP);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.DELETE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }
        
        if (args.size() < 1) {
            return false;
        }
        
        String targetSessionName = args.remove(0);
        VoteSession targetVoteSession = this.voteSessionManager.getVoteSession(targetSessionName);

        if (targetVoteSession == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        this.voteSessionManager.deleteSession(targetVoteSession);
        sender.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_DELETED, targetSessionName));

        return true;
    }
    
}
