package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

public class UnvoteCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;
    
    public UnvoteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigurationNodes.UNVOTE_COMMAND_HELP);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }
        
        if (!sender.hasPermission(PermissionNodes.UNVOTE)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }
        
        if (args.size() < 2) {
            return false;
        }
        
        Player player = (Player) sender;
        String sessionName = args.remove(0);
        String votePointName = args.remove(0);

        VoteSession session = this.voteSessionManager.getVoteSession(sessionName);
        if (session == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }
        
        VotePoint votePoint = session.getVotePoint(votePointName);
        if (votePoint == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.VOTEPOINT_DOES_NOT_EXIST));
            return true;
        }
        
        try {
            session.unvote(player, votePoint);
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigurationNodes.F_UNVOTED, votePointName));
        } catch (VotePointNotVotedException exception) {
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigurationNodes.F_NOT_VOTED, votePointName));
        }

        return true;
    }
    
}
