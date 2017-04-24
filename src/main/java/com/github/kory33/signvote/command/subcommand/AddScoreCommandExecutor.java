package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MagicNumbers;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;

public class AddScoreCommandExecutor extends SubCommandExecutor{
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;

    public AddScoreCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    @Override
    protected String getHelpString() {
        return messageConfiguration.getString(MessageConfigurationNodes.ADD_SCORE_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.MODIFY_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }

        if (args.size() < 3) {
            return false;
        }

        String voteSessionname = args.remove(0);
        VoteSession session = this.voteSessionManager.getVoteSession(voteSessionname);

        if (session == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        int score, limit;
        try {
            score = Integer.parseInt(args.remove(0));
            limit = Integer.parseInt(args.remove(0));
        } catch (NumberFormatException e) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.INVALID_NUMBER));
            return true;
        }

        String permission = PermissionNodes.VOTE;
        if (!args.isEmpty()) {
            permission = args.remove(0);
            if (permission == "op") {
                permission = PermissionNodes.VOTE_MORE;
            }
        }

        try {
            session.getVoteScoreCountLimits().addLimit(score, permission, limit);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigurationNodes.INVALID_NUMBER));
            return true;
        }

        String limitString = limit == MagicNumbers.VOTELIMIT_INFINITY ? "Infinity" : String.valueOf(limit);
        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigurationNodes.F_SCORE_LIMIT_ADDED,
                limitString, score, session.getName(), permission));
        return true;
    }
}
