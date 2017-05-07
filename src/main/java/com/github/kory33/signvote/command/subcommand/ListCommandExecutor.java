package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.apache.commons.lang.math.NumberUtils;
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
import com.github.kory33.signvote.ui.player.ListSessionInterface;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;

public class ListCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager voteSessionManager;
    private final RunnableHashTable runnableHashTable;
    private final PlayerInteractiveInterfaceManager interfaceManager;

    public ListCommandExecutor(SignVote plugin) {
        this.messageConfig = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.runnableHashTable = plugin.getRunnableHashTable();
        this.interfaceManager = plugin.getInterfaceManager();
    }

    @Override
    protected String getHelpString() {
        return messageConfig.getString(MessageConfigNodes.LIST_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.LIST_SESSION)) {
            sender.sendMessage(messageConfig.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(messageConfig.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS));
            return true;
        }

        Player player = (Player) sender;
        String pageIndexString = args.size() != 0 ? args.remove(0) : String.valueOf("0");
        int pageIndex = NumberUtils.isNumber(pageIndexString) ? NumberUtils.createInteger(pageIndexString) : 0;

        PlayerClickableChatInterface listInterface = new ListSessionInterface(player, voteSessionManager, messageConfig, runnableHashTable, interfaceManager, pageIndex);
        interfaceManager.registerInterface(listInterface);
        listInterface.send();

        return true;
    }
}
