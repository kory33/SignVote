package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import com.github.kory33.chatgui.command.RunnableInvoker;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.chatgui.model.ChatInterface;
import com.github.kory33.signvote.ui.console.ConsoleListSessionInterface;
import com.github.kory33.signvote.ui.player.ListSessionInterface;
import com.github.kory33.chatgui.model.player.PlayerClickableChatInterface;

/**
 * Executor class of "list" sub-command
 * @author Kory
 */
public class ListCommandExecutor implements SubCommandExecutor {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager voteSessionManager;
    private final RunnableInvoker runnableInvoker;
    private final PlayerInteractiveInterfaceManager interfaceManager;

    public ListCommandExecutor(SignVote plugin) {
        this.messageConfig = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.runnableInvoker = plugin.getRunnableInvoker();
        this.interfaceManager = plugin.getInterfaceManager();
    }

    @Override
    public String getHelpString() {
        return messageConfig.getString(MessageConfigNodes.LIST_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.LIST_SESSION)) {
            sender.sendMessage(messageConfig.getString(MessageConfigNodes.MISSING_PERMS));
            return true;
        }

        if (!(sender instanceof Player)) {
            ChatInterface listInterface = new ConsoleListSessionInterface(voteSessionManager, messageConfig);
            listInterface.send(sender);
            return true;
        }

        Player player = (Player) sender;
        String pageIndexString = args.size() != 0 ? args.remove(0) : String.valueOf("0");
        int pageIndex = NumberUtils.isNumber(pageIndexString) ? NumberUtils.createInteger(pageIndexString) : 0;

        PlayerClickableChatInterface listInterface = new ListSessionInterface(player, voteSessionManager, messageConfig, runnableInvoker, interfaceManager, pageIndex);
        interfaceManager.registerInterface(listInterface);
        listInterface.send();

        return true;
    }
}
