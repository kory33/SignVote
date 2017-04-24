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

        try {
            String voteSessionname = args.remove(0);
            VoteSession session = this.voteSessionManager.getVoteSession(voteSessionname);

            int score = new Integer(args.remove(0));
            int limit = new Integer(args.remove(0));

            String permission = PermissionNodes.VOTE;
            if (!args.isEmpty()) {
                permission = args.remove(0);
                if (permission == "op") {
                    permission = PermissionNodes.VOTE_MORE;
                }
            }

            session.getVoteScoreCountLimits().addLimit(score, permission, limit);
        } catch (Exception exception) {
            return false;
        }

        return true;
    }
}
