package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class ListSessionInterface extends ChatInterface {
    private final Stream<VoteSession> sessionStream;
    private final JSONConfiguration messageConfig;

    public ListSessionInterface(Stream<VoteSession> sessionStream, JSONConfiguration messageConfig) {
        this.sessionStream = sessionStream;
        this.messageConfig = messageConfig;
    }

    private MessageParts getConfigMessagePart(String configurationNode) {
        return new MessageParts(this.messageConfig.getString(configurationNode));
    }

    private String getSessionInfoLine(VoteSession session) {
        String prefix = messageConfig.getString(MessageConfigurationNodes.LIST_UI_LINE_PREFIX);
        String sessionName = session.getName();
        String sessionState = "";
        if (session.isOpen()) {
            sessionState = messageConfig.getString(MessageConfigurationNodes.LIST_UI_SESSION_OPEN);
        } else {
            sessionState = messageConfig.getString(MessageConfigurationNodes.LIST_UI_SESSION_CLOSED);
        }

        return prefix + sessionName + sessionState + "\n";
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        MessageParts header = this.getConfigMessagePart(MessageConfigurationNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigurationNodes.UI_FOOTER);

        MessageParts heading = this.getConfigMessagePart(MessageConfigurationNodes.LIST_UI_HEADING);

        ArrayList<MessageParts> messageList = new ArrayList<>();
        messageList.add(header);
        messageList.add(heading);

        this.sessionStream
            .map(session -> new MessageParts(this.getSessionInfoLine(session)))
            .forEach(messageList::add);

        messageList.add(footer);

        return new MessageComponent(messageList);
    }
}
