package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.ChatInterface;
import com.github.kory33.signvote.ui.ListSessionInterface;

public class ListCommandExecutor extends SubCommandExecutor {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager voteSessionManager;

    public ListCommandExecutor(SignVote plugin) {
        this.messageConfig = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }

    @Override
    protected String getHelpString() {
        return messageConfig.getString(MessageConfigurationNodes.LIST_COMMAND_HELP);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.LIST_SESSION)) {
            sender.sendMessage(messageConfig.getString(MessageConfigurationNodes.MISSING_PERMS));
            return true;
        }

        Stream<VoteSession> sessions = this.voteSessionManager.getVoteSessionStream();
        ChatInterface listInterface = new ListSessionInterface(sessions, this.messageConfig);
        listInterface.send(sender);

        return true;
    }
}
