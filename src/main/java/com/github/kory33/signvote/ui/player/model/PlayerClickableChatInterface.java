package com.github.kory33.signvote.ui.player.model;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

import lombok.Getter;

/**
 * A class representing a player chat interface which is capable of navigating the player
 * through clicks on the buttons
 * @author kory
 */
public abstract class PlayerClickableChatInterface extends PlayerChatInterface {
    @Getter private final RunnableHashTable runnableHashTable;
    private boolean isValidSession;

    private final BijectiveHashMap<MessageParts, Long> buttonIdMapping;

    public PlayerClickableChatInterface(Player player, RunnableHashTable runnableHashTable) {
        super(player);

        this.runnableHashTable = runnableHashTable;
        this.isValidSession = true;
        this.buttonIdMapping = new BijectiveHashMap<>();
    }

    protected boolean isValidSession() {
        return this.isValidSession;
    }

    /**
     * Revoke all runnables bound to this interface.
     */
    protected final void revokeAllRunnables() {
        // remove all the bound runnable objects
        this.buttonIdMapping.getInverse().keySet().forEach(this.runnableHashTable::cancelTask);
        this.buttonIdMapping.keySet().forEach(this.buttonIdMapping::remove);
    }

    /**
     * Revoke a particular button
     */
    protected final void revokeButton(MessageParts buttonMessagePart) {
        Long runnableId = this.buttonIdMapping.get(buttonMessagePart);
        if (runnableId == null) {
            return;
        }

        this.runnableHashTable.cancelTask(runnableId);
        this.buttonIdMapping.remove(buttonMessagePart);
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
     * Get a message component which invokes the given runnable object when clicked.
     * @param runnable runnable to be run(synchronously) when the player clicks the button
     * @param buttonString message that gets displayed as the button
     * @return a button message component that is bound to the runnable object
     */
    protected final MessageParts getButton(Runnable runnable, String buttonString) {
        MessageParts button = new MessageParts(buttonString);
        long runnableId = this.runnableHashTable.registerRunnable(runnable);

        String command = String.join(" ", this.getRunCommandRoot(), String.valueOf(runnableId));
        button.setClickEvent(ClickEventType.RUN_COMMAND, command);

        this.buttonIdMapping.put(button, runnableId);
        return button;
    }

    /**
     * Cancel the action aimed by the interface and revoke the interface object
     */
    protected void cancelAction(String cancelMessage) {
        if (!this.isValidSession) {
            return;
        }

        this.revokeSession();
        this.targetPlayer.sendMessage(cancelMessage);
    }

    /**
     * Get the body of the chat interface.
     * Returned body is enclosed by a header and a footer and is finally sent to the player.
     * @return a list of messages that represents a body of the interface
     */
    protected abstract MessagePartsList getBodyMessages();

    /**
     * Get the header line of the interface
     * @return message component list representing the header
     */
    protected abstract MessagePartsList getInterfaceHeader();

    /**
     * Get the footer line of the interface
     * @return message component list representing the footer
     */
    protected abstract MessagePartsList getInterfaceFooter();

    /**
     * Get a root component of the "run command" which is executed when a player clicks on a button.
     * @return run command root string
     */
    public abstract String getRunCommandRoot();

    /**
     * Construct the interface in a form of:<br>
     * [header]<br>
     * [body content] <br>
     * [footer]<br>
     * <br>
     * Override this method only if the interface should be in other form.<br>
     * Otherwise, [body content] part should be constructed by {@link PlayerClickableChatInterface#getBodyMessages()} method.
     */
    @Override
    protected MessagePartsList constructInterfaceMessages() {
        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(this.getInterfaceHeader());
        messagePartsList.addAll(this.getBodyMessages());
        messagePartsList.addAll(this.getInterfaceFooter());

        return messagePartsList;
    }
}
