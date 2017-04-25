package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.listners.PlayerChatInterceptor;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public abstract class PlayerInteractiveChatInterface extends PlayerChatInterface {
    protected final JSONConfiguration messageConfig;
    protected final RunnableHashTable runnableHashTable;
    private boolean isValidSession;

    private final Set<Long> registeredRunnableIds;

    private final PlayerChatInterceptor chatInterceptor;

    public PlayerInteractiveChatInterface(Player player, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerChatInterceptor chatInterceptor) {
        super(player);
        this.messageConfig = messageConfiguration;
        this.runnableHashTable = runnableHashTable;
        this.isValidSession = true;
        this.registeredRunnableIds = new HashSet<>();

        this.chatInterceptor = chatInterceptor;
    }

    protected boolean isValidSession() {
        return this.isValidSession;
    }

    /**
     * Nullify the session.
     * Clear all associated runnables from cache table
     * and mark the session as invalid.
     */
    public void revokeSession() {
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

    /**
     * Get an arraylist representing a form to which the target player can input string data.
     * @param onPlayerSendString
     * @param name
     * @param displayValue
     * @param defaultDisplayValue
     * @return
     */
    protected ArrayList<MessageParts> getForm(Consumer<String> onPlayerSendString, String name, String value) {
        MessageParts formName = new MessageParts(this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_NAME, name));

        MessageParts defaultDisplay = new MessageParts(this.messageConfig.getString(MessageConfigNodes.UI_FORM_NOTSET));
        MessageParts formValue = value != null && !value.isEmpty() ? defaultDisplay : new MessageParts(value);

        MessageParts editButton = this.getButton(() -> {
            chatInterceptor.interceptFirstMessageFrom(this.targetPlayer)
                .thenAccept(onPlayerSendString).thenRun(this::send)
                .exceptionally((error) -> null);
        }, this.getConfigMessagePart(MessageConfigNodes.UI_EDIT_BUTTON));


        ArrayList<MessageParts> form = new ArrayList<>();

        form.add(formName);
        form.add(formValue);
        form.add(editButton);
        form.add(new MessageParts("\n"));

        return form;
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
