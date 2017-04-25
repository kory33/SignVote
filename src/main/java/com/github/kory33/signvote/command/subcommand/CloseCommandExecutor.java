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

public class CloseCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public CloseCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigNodes.CLOSE_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.CLOSE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (args.size() < 1) {
            return false;
        }
        
        String closeTargetSessionName = args.remove(0);
        VoteSession targetVoteSession = this.voteSessionManager.getVoteSession(closeTargetSessionName);
        
        if (targetVoteSession == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }
        
        if (!targetVoteSession.isOpen()) {
            sender.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_ALREADY_CLOSED, closeTargetSessionName));
            return true;
        }
        
        targetVoteSession.setOpen(false);
        String completionMessage = this.messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_CLOSED, closeTargetSessionName);
        sender.sendMessage(completionMessage);
        return true;
    }
}
