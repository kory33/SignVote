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

public class CreateCommandExecutor extends SubCommandExecutor{
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public CreateCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.CREATE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        String voteSessionName = args.remove(0);

        if (this.voteSessionManager.getVoteSession(voteSessionName) != null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.SESSION_ALREADY_EXISTS));
            return true;
        }
        
        try {
            this.voteSessionManager.addSession(new VoteSession(voteSessionName));
        } catch (Exception exception) {
            return false;
        }
        
        return true;
    }

    @Override
    protected String getHelpString() {
        return messageConfiguration.getString(MessageConfigurationNodes.CREATE_COMMAND_HELP);
    }
}
