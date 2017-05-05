package com.github.kory33.signvote.ui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * A class representing a player chat interface which is capable of navigating the player
 * through clicks on the buttons
 * @author kory
 */
public abstract class PlayerClickableChatInterface extends PlayerChatInterface {
    protected final JSONConfiguration messageConfig;
    private final RunnableHashTable runnableHashTable;
    private boolean isValidSession;

    private final Set<Long> registeredRunnableIds;

    public PlayerClickableChatInterface(Player player, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable) {
        super(player);

        this.messageConfig = messageConfiguration;
        this.runnableHashTable = runnableHashTable;
        this.isValidSession = true;
        this.registeredRunnableIds = new HashSet<>();
    }

    protected boolean isValidSession() {
        return this.isValidSession;
    }

    /**
     * Revoke all runnables bound to this interface.
     */
    protected void revokeAllRunnables() {
        // remove all the bound runnables
        for (long runnableId: this.registeredRunnableIds) {
            this.runnableHashTable.cancelTask(runnableId);
        }
    }

    /**
     * Nullify the session.
     * Clear all associated runnables from cache table
     * and mark the session as invalid.
     */
    public void revokeSession() {
        this.isValidSession = false;
        this.revokeAllRunnables();
    }

    protected MessageParts getConfigMessagePart(String configurationNode) {
        return new MessageParts(this.messageConfig.getString(configurationNode));
    }

    protected MessageParts getButton(String command) {
        MessageParts button = this.getConfigMessagePart(MessageConfigNodes.UI_BUTTON);
        button.setClickEvent(ClickEventType.RUN_COMMAND, command);
        return button;
    }

    protected MessageParts getButton(Runnable runnable, MessageParts button) {
        long runnableId = TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, runnable);
        this.registeredRunnableIds.add(runnableId);
        return button;
    }

    protected MessageParts getButton(Runnable runnable) {
        MessageParts button = this.getConfigMessagePart(MessageConfigNodes.UI_BUTTON);
        return this.getButton(runnable, button);
    }

    protected void cancelAction() {
        if (!this.isValidSession) {
            return;
        }

        this.revokeSession();
        String message = this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED);
        this.targetPlayer.sendMessage(message);
    }
}
