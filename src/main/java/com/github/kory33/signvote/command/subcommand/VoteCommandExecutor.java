package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.exception.InvalidScoreVotedException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

public class VoteCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public VoteCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    @Override
    protected String getHelpString() {
        return this.messageConfiguration.getString(MessageConfigNodes.VOTE_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }

        if (!sender.hasPermission(PermissionNodes.VOTE)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (args.size() != 3) {
            return false;
        }

        Player player = (Player) sender;
        String sessionName = args.remove(0);
        String votePointName = args.remove(0);
        String voteScoreString = args.remove(0);

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

        int voteScore;
        try {
            voteScore = Integer.parseInt(voteScoreString);
        } catch (Exception exception) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE));
            return true;
        }

        // if the voter does not have vote score reserved
        Optional<Integer> reservedVotes = session.getReservedVoteCounts(player).get(voteScore);
        if (reservedVotes == null || reservedVotes.orElse(-1) == 0) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE));
            return true;
        }

        try {
            session.vote(player, votePoint, voteScore);
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTED));
        } catch (ScoreCountLimitReachedException exception) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.REACHED_VOTE_SCORE_LIMIT));
        } catch (VotePointAlreadyVotedException exception) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_ALREADY_VOTED));
        } catch (InvalidScoreVotedException exception) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE));
        }

        return true;
    }

}
