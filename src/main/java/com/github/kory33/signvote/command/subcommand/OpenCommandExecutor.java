package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

public class OpenCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public OpenCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigurationNodes.OPEN_COMMAND_HELP);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (args.size() < 1) {
            return false;
        }
        
        String closeTargetSessionName = args.remove(0);
        VoteSession targetVoteSession = this.voteSessionManager.getVoteSession(closeTargetSessionName);
        
        if (targetVoteSession == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }
        
        if (targetVoteSession.isOpen()) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.SESSION_ALREADY_OPENED));
            return true;
        }
        
        targetVoteSession.setOpen(true);
        return true;
    }
    
}
