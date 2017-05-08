package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

public class CreateCommandExecutor implements SubCommandExecutor{
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;
    private final Logger pluginLogger;

    public CreateCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.pluginLogger = plugin.getLogger();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.CREATE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }
        
        if (args.size() < 1) {
            return false;
        }
        
        String voteSessionName = args.remove(0);

        if (this.voteSessionManager.getVoteSession(voteSessionName) != null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.SESSION_ALREADY_EXISTS));
            return true;
        }
        
        try {
            this.voteSessionManager.addSession(new VoteSession(voteSessionName));
        } catch (Exception exception) {
            this.pluginLogger.log(Level.SEVERE, "Error while creating session: ", exception);
            return false;
        }
        
        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_CREATED, voteSessionName));
        return true;
    }

    @Override
    public String getHelpString() {
        return messageConfiguration.getString(MessageConfigNodes.CREATE_COMMAND_HELP);
    }
}
