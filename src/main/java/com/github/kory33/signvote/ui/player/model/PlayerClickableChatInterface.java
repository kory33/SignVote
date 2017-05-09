package com.github.kory33.signvote.ui.player.model;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

import lombok.Getter;

/**
 * A class representing a player chat interface which is capable of navigating the player
 * through clicks on the buttons
 * @author kory
 */
public abstract class PlayerClickableChatInterface extends PlayerChatInterface {
    protected final JSONConfiguration messageConfig;
    @Getter private final RunnableHashTable runnableHashTable;
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
    protected final void revokeAllRunnables() {
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

    /**
     * Get a message formatted with the given array of Object arguments(optional)
     * @param configurationNode configuration node from which the message should be fetched
     * @param objects objects used in formatting the fetched string
     * @return formatted message component
     */
    protected final MessageParts getFormattedMessagePart(String configurationNode, Object... objects) {
        return new MessageParts(this.messageConfig.getFormatted(configurationNode, objects));
    }

    /**
     * Get a message component which invokes the given runnable object when clicked.
     * @param runnable runnable to be run(synchronously) when the player clicks the button
     * @param button message that gets displayed as the button
     * @return a button message component that is bound to the runnable object
     */
    protected final MessageParts getButton(Runnable runnable, MessageParts button) {
        long runnableId = TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, runnable);
        this.registeredRunnableIds.add(runnableId);
        return button;
    }

    /**
     * Get a message component which invokes the given runnable object when clicked.
     * Displayed button message defaults to string at {@value MessageConfigNodes#UI_BUTTON}
     * Use {@link PlayerClickableChatInterface#getButton(Runnable, MessageParts)} to configure button display.
     * @param runnable runnable to be run(synchronously) when the player clicks the button
     * @return a button message component that is bound to the runnable object.
     */
    protected final MessageParts getButton(Runnable runnable) {
        MessageParts button = this.getFormattedMessagePart(MessageConfigNodes.UI_BUTTON);
        return this.getButton(runnable, button);
    }

    /**
     * Cancel the action aimed by the interface and revoke the interface object
     */
    protected void cancelAction() {
        if (!this.isValidSession) {
            return;
        }

        this.revokeSession();
        String message = this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED);
        this.targetPlayer.sendMessage(message);
    }

    /**
     * Get the body of the chat interface.
     * Returned body is enclosed by a header and a footer and is finally sent to the player.
     * @return a list of messages that represents a body of the interface
     */
    protected abstract MessagePartsList getBodyMessages();

    @Override
    protected MessagePartsList constructInterfaceMessages() {
        MessageParts header = this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(header);
        messagePartsList.addAll(this.getBodyMessages());
        messagePartsList.add(footer);

        return messagePartsList;
    }
}
