package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private final RunnableHashTable runnableHashTable;
    private boolean isValidSession;

    private final Set<Long> registeredRunnableIds;

    protected final PlayerChatInterceptor chatInterceptor;

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
     * Revoke all runnables bound to this interface.
     */
    private void revokeAllRunnables() {
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
        this.chatInterceptor.cancelAnyInterception(this.targetPlayer, "UI session has been revoked.");
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

    private void promptInput(String formName) {
        String message = this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_PROMPT, formName);
        this.targetPlayer.sendMessage(message);
    }

    private void notifyInvalidInput() {
        String message = this.messageConfig.getString(MessageConfigNodes.UI_FORM_INVALID_INPUT);
        this.targetPlayer.sendMessage(message);
    }

    private CompletableFuture<Void> getInputToForm(Consumer<String> onPlayerSendString, Predicate<String> validator, String formName) {
        return chatInterceptor.interceptFirstMessageFrom(this.targetPlayer)
                .thenAccept(input -> {
                    if (!validator.test(input)) {
                        this.notifyInvalidInput();
                        this.getInputToForm(onPlayerSendString, validator, formName);
                        return;
                    }
                    onPlayerSendString.accept(input);
                    this.send();
                })
                .exceptionally((error) -> null);
    }

    /**
     * Get an arraylist representing a form to which the target player can input string data.
     * @param onPlayerSendString
     * @param name
     * @param displayValue
     * @param defaultDisplayValue
     * @return
     */
    protected ArrayList<MessageParts> getForm(Consumer<String> onPlayerSendString, Predicate<String> validator, String name, String value) {
        MessageParts formName = new MessageParts(this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_NAME, name));

        if (value == null || value.isEmpty()) {
            value = this.messageConfig.getString(MessageConfigNodes.UI_FORM_NOTSET);
        }
        MessageParts formValue = new MessageParts(this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_VALUE, value));

        MessageParts editButton = this.getButton(() -> {
            this.revokeAllRunnables();
            this.promptInput(name);
            this.getInputToForm(onPlayerSendString, validator, name);
        }, this.getConfigMessagePart(MessageConfigNodes.UI_FORM_EDIT_BUTTON));

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
