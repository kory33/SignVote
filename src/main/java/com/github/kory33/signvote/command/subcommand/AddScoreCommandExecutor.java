package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MagicNumbers;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.listeners.PlayerChatInterceptor;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.PlayerAddscoreInterface;
import com.github.kory33.signvote.ui.PlayerFormChatInterface;

public class AddScoreCommandExecutor extends SubCommandExecutor{
    private final JSONConfiguration messageConfiguration;
    private final VoteSessionManager voteSessionManager;
    private final RunnableHashTable runnableHashTable;
    private final PlayerInteractiveInterfaceManager interfaceManager;
    private final PlayerChatInterceptor chatInterceptor;

    public AddScoreCommandExecutor(SignVote plugin) {
        this.messageConfiguration = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.runnableHashTable = plugin.getRunnableHashTable();
        this.interfaceManager = plugin.getInterfaceManager();
        this.chatInterceptor = plugin.getChatInterceptor();
    }

    @Override
    protected String getHelpString() {
        return messageConfiguration.getString(MessageConfigNodes.ADD_SCORE_COMMAND_HELP);
    }

    /**
     * Construct and send addscore interface to the player
     * which adds a votelimit to the given session
     * @param player
     * @param session
     */
    private void sendAddScoreInterface(Player player, VoteSession session) {
        PlayerFormChatInterface chatInterface = new PlayerAddscoreInterface(player, session, messageConfiguration,
                runnableHashTable, chatInterceptor);
        this.interfaceManager.registerInterface(chatInterface);
        chatInterface.send();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.MODIFY_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (args.size() == 0) {
            return false;
        }

        String voteSessionname = args.remove(0);
        VoteSession session = this.voteSessionManager.getVoteSession(voteSessionname);

        if (session == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        if (args.size() < 2 && sender instanceof Player) {
            this.sendAddScoreInterface((Player)sender, session);
            return true;
        } else if (args.size() < 2) {
            return false;
        }

        int score, limit;
        try {
            score = Integer.parseInt(args.remove(0));
            limit = Integer.parseInt(args.remove(0));
        } catch (NumberFormatException e) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_NUMBER));
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
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_NUMBER));
            return true;
        }

        String limitString = limit == MagicNumbers.VOTELIMIT_INFINITY ? "Infinity" : String.valueOf(limit);
        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigNodes.F_SCORE_LIMIT_ADDED,
                limitString, score, session.getName(), permission));
        return true;
    }
}
