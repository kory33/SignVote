package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

/**
 * Executor class of "unvote" sub-command
 * @author Kory
 */
public class UnvoteCommandExecutor implements SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public UnvoteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    @Override
    public String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigNodes.UNVOTE_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }

        if (!sender.hasPermission(PermissionNodes.UNVOTE)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
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
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        VotePoint votePoint = session.getVotePoint(votePointName);
        if (votePoint == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_DOES_NOT_EXIST));
            return true;
        }

        if (!session.isOpen()) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTE_SESSION_CLOSED));
            return true;
        }

        try {
            session.getVoteManager().removeVote(player.getUniqueId(), votePoint);
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.F_UNVOTED, votePointName));
        } catch (VotePointNotVotedException exception) {
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.NOT_VOTED, votePointName));
        }

        return true;
    }

}
