package com.github.kory33.signvote.ui;

import java.util.HashSet;
import java.util.Set;

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

    private final Set<Long> registeredRunnableIds;

    public PlayerInteractiveChatInterface(Player player, JSONConfiguration messageConfiguration, RunnableHashTable runnableHashTable) {
        super(player);
        this.messageConfig = messageConfiguration;
        this.runnableHashTable = runnableHashTable;
        this.isValidSession = true;
        this.registeredRunnableIds = new HashSet<>();
    }

    protected boolean isValidSession() {
        return this.isValidSession;
    }

    protected void revokeSession() {
        this.isValidSession = false;
        // remove all the bound runnables
        for (long runnableId: this.registeredRunnableIds) {
            this.runnableHashTable.cancelTask(runnableId);
        }
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
        long runnableId = TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, runnable);
        this.registeredRunnableIds.add(runnableId);
        return button;
    }

    protected void cancelAction() {
        if (!this.isValidSession) {
            return;
        }

        this.revokeSession();
        String message = this.messageConfig.getString(MessageConfigurationNodes.UI_CANCELLED);
        this.targetPlayer.sendMessage(message);
    }
}
