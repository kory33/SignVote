package com.github.kory33.signvote.ui.console;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.ChatInterface;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * A class which represents a console interface(non-interactive)
 * which displays a list of existing sessions
 * @author Kory
 */
public class ConsoleListSessionInterface extends ChatInterface {
    private final VoteSessionManager sessionManager;
    private final JSONConfiguration messageConfig;

    public ConsoleListSessionInterface(VoteSessionManager voteSessionManager, JSONConfiguration messageConfig) {
        this.sessionManager = voteSessionManager;
        this.messageConfig = messageConfig;
    }

    private MessageParts getFormattedMessagePart(String configurationNode, Object... objects) {
        return new MessageParts(this.messageConfig.getFormatted(configurationNode, objects));
    }

    private MessageParts getSessionInfoLine(VoteSession session) {
        String openStateNode = session.isOpen() ? MessageConfigNodes.LIST_UI_SESSION_OPEN : MessageConfigNodes.LIST_UI_SESSION_OPEN;
        String sessionState = messageConfig.getString(openStateNode);

        return getFormattedMessagePart(MessageConfigNodes.F_LIST_UI_ENTRY_TEMPLATE, session.getName(), sessionState);
    }

    @Override
    protected MessagePartsList constructInterfaceMessages() {
        MessageParts header = this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER);

        MessageParts heading = this.getFormattedMessagePart(MessageConfigNodes.LIST_UI_HEADING);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(header);
        messagePartsList.addLine(heading);

        this.sessionManager.getVoteSessionStream()
            .map(this::getSessionInfoLine)
            .forEach(messagePartsList::addLine);

        messagePartsList.add(footer);

        return messagePartsList;
    }
}
