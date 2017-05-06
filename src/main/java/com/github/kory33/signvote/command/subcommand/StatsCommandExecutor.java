package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.StatsTypeSelectionInterface;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;
import com.github.kory33.signvote.ui.player.stats.StatsInterface;

public class StatsCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager sessionManager;
    private final RunnableHashTable runnableHashTable;
    private final PlayerInteractiveInterfaceManager interfaceManager;

    public StatsCommandExecutor(SignVote signVote) {
        this.messageConfig = signVote.getMessagesConfiguration();
        this.sessionManager = signVote.getVoteSessionManager();
        this.runnableHashTable = signVote.getRunnableHashTable();
        this.interfaceManager = signVote.getInterfaceManager();
    }

    @Override
    protected String getHelpString() {
        return this.messageConfig.getString(MessageConfigNodes.STATS_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.VIEW_STATS)) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }

        Player player = (Player)sender;

        if (args.size() == 0) {
            return false;
        }

        VoteSession session = this.sessionManager.getVoteSession(args.remove(0));
        if (session == null) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return true;
        }

        PlayerClickableChatInterface chatInterface;
        if (args.size() == 0) {
            chatInterface = new StatsTypeSelectionInterface(player, session, messageConfig, runnableHashTable, interfaceManager);
        } else {
            String statsType;
            if (args.size() == 0) {
                statsType = args.remove(0);
            } else {
                statsType = null;
            }

            int pageIndex;
            if (args.size() == 0) {
                pageIndex = 0;
            } else {
                try {
                    pageIndex = Integer.parseInt(args.remove(0));
                } catch (NumberFormatException exception) {
                    pageIndex = 0;
                }
            }

            chatInterface = StatsInterface.createNewInterface(player, session, statsType, pageIndex, messageConfig, runnableHashTable, interfaceManager);

            if (chatInterface == null) {
                sender.sendMessage(messageConfig.getString(MessageConfigNodes.STATS_INVALID_TYPE));
                return true;
            }
        }

        chatInterface.send();
        this.interfaceManager.registerInterface(chatInterface);

        return true;
    }
}
