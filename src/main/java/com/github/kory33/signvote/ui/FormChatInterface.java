package com.github.kory33.signvote.ui;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.listeners.PlayerChatInterceptor;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * A class representing a player chat interface which is capable of accepting
 * player's input through its input forms.
 * @author kory
 */
public abstract class FormChatInterface extends PlayerClickableChatInterface {
    protected final PlayerChatInterceptor chatInterceptor;

    public FormChatInterface(Player player, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerChatInterceptor chatInterceptor) {
        super(player, messageConfiguration, runnableHashTable);
        this.chatInterceptor = chatInterceptor;
    }

    /**
     * Nullify the session.
     * Clear all associated runnables from cache table
     * and mark the session as invalid.
     */
    @Override
    public void revokeSession() {
        super.revokeSession();
        this.chatInterceptor.cancelAnyInterception(this.targetPlayer, "UI session has been revoked.");
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
    protected MessagePartsList getForm(Consumer<String> onPlayerSendString, Predicate<String> validator, String name, String value) {
        MessageParts formName = this.getFormattedMessagePart(MessageConfigNodes.F_UI_FORM_NAME, name);

        if (value == null || value.isEmpty()) {
            value = this.messageConfig.getString(MessageConfigNodes.UI_FORM_NOTSET);
        }
        MessageParts formValue = this.getFormattedMessagePart(MessageConfigNodes.F_UI_FORM_VALUE, value);

        MessageParts editButton = this.getButton(() -> {
            this.revokeAllRunnables();
            this.promptInput(name);
            this.getInputToForm(onPlayerSendString, validator, name);
        }, this.getFormattedMessagePart(MessageConfigNodes.UI_FORM_EDIT_BUTTON));

        MessagePartsList form = new MessagePartsList();

        form.add(formName);
        form.add(formValue);
        form.add(editButton);
        form.addLine("");

        return form;
    }
}
