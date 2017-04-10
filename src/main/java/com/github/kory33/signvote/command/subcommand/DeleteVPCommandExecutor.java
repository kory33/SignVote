package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.avaje.ebeaninternal.server.cluster.mcast.Message;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

public class DeleteVPCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public DeleteVPCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigurationNodes.DELETE_VP_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (args.size() < 2) {
            return false;
        }
        
        String sessionName = args.remove(0);
        String votepointName = args.remove(0);
        
        VoteSession session = this.voteSessionManager.getVoteSession(sessionName);
        if (session == null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }
        
        VotePoint votePoint = session.getVotePoint(votepointName);
        if (votePoint == null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.VOTEPOINT_DOES_NOT_EXIST));
        }
        
        session.deleteVotepoint(votePoint);
        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigurationNodes.F_VOTEPOINT_DELETED, sessionName,
                votepointName));
        
        return true;
    }
    
}
