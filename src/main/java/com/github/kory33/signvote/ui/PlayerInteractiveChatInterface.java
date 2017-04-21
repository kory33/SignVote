package com.github.kory33.signvote.ui;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public abstract class PlayerInteractiveChatInterface extends PlayerChatInterface {
    protected final JSONConfiguration messageConfig;
    protected final RunnableHashTable runnableHashTable;
    private boolean isValidSession;

    public PlayerInteractiveChatInterface(Player player, JSONConfiguration messageConfiguration, RunnableHashTable runnableHashTable) {
        super(player);
        this.messageConfig = messageConfiguration;
        this.runnableHashTable = runnableHashTable;
        this.isValidSession = true;
    }

    protected boolean isValidSession() {
        return this.isValidSession;
    }

    protected void setValidSession(boolean value) {
        this.isValidSession = value;
    }

    protected MessageParts getConfigMessagePart(String configurationNode) {
        return new MessageParts(this.messageConfig.getString(configurationNode));
    }

    protected MessageParts getButton(String command) {
        MessageParts button = this.getConfigMessagePart(MessageConfigurationNodes.UI_BUTTON);
        button.setClickEvent(ClickEventType.RUN_COMMAND, command);
        return button;
    }

    protected MessageParts getButton(Runnable runnable) {
        MessageParts button = this.getConfigMessagePart(MessageConfigurationNodes.UI_BUTTON);
        TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, runnable);
        return button;
    }

    protected void cancelAction() {
        if (!this.isValidSession) {
            return;
        }

        this.setValidSession(false);
        String message = this.messageConfig.getString(MessageConfigurationNodes.UI_CANCELLED);
        this.targetPlayer.sendMessage(message);
    }
}
