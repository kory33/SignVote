package com.github.kory33.signvote.ui.player.model;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.listeners.PlayerChatInterceptor;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class representing a player chat interface which is capable of accepting
 * player's input through its input forms.
 * @author kory
 */
public abstract class FormChatInterface extends PlayerClickableChatInterface {
    private final PlayerChatInterceptor chatInterceptor;
    private final JSONConfiguration messageConfig;

    private Long formInputCancelRunnableId;

    public FormChatInterface(Player player, JSONConfiguration messageConfig,
            RunnableHashTable runnableHashTable, PlayerChatInterceptor chatInterceptor) {
        super(player, runnableHashTable);
        this.chatInterceptor = chatInterceptor;
        this.formInputCancelRunnableId = null;
        this.messageConfig = messageConfig;
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
        this.revokeCancelInputButton();
    }

    private void revokeCancelInputButton() {
        if (this.formInputCancelRunnableId != null) {
            this.getRunnableHashTable().cancelTask(this.formInputCancelRunnableId);
            this.formInputCancelRunnableId = null;
        }
    }

    private MessageParts getCancelInputButton() {
        MessageParts button = new MessageParts(messageConfig.getString(MessageConfigNodes.UI_CANCEL_INPUT_BUTTON));
        formInputCancelRunnableId = TellRawUtility.bindRunnableToMessageParts(getRunnableHashTable(), button, () -> {
            this.chatInterceptor.cancelAnyInterception(this.targetPlayer, "Input cancelled.");
            this.targetPlayer.sendMessage(this.messageConfig.getString(MessageConfigNodes.UI_FOOTER));
            this.targetPlayer.sendMessage(this.messageConfig.getString(MessageConfigNodes.UI_INPUT_CANCELLED));
            this.revokeCancelInputButton();
            this.send();
        });

        return button;
    }

    private void promptInput(String formName) {
        String message = this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_PROMPT, formName);
        MessageParts cancelInputButton = this.getCancelInputButton();

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.add(message);
        messagePartsList.add(" ");
        messagePartsList.add(cancelInputButton);

        (new MessageComponent(messagePartsList)).send(this.targetPlayer);
    }

    private void notifyInvalidInput() {
        String message = this.messageConfig.getString(MessageConfigNodes.UI_FORM_INVALID_INPUT);
        this.targetPlayer.sendMessage(message);
    }

    private void getInputToForm(Consumer<String> onPlayerSendString, Predicate<String> validator) {
        chatInterceptor.interceptFirstMessageFrom(this.targetPlayer)
                .thenAccept(input -> {
                    if (!validator.test(input)) {
                        this.notifyInvalidInput();
                        this.getInputToForm(onPlayerSendString, validator);
                        return;
                    }
                    onPlayerSendString.accept(input);
                    this.send();
                })
                .exceptionally((error) -> null);
    }

    /**
     * Get a string that is used as a button to edit the value in the form.
     * @return a string that represents an edit-button
     */
    protected abstract String getEditButtonString();

    /**
     * Get a label component string
     * @param labelName name of the form field
     * @return a formatted string that gets displayed as a label of a field.
     */
    protected abstract String getLabelString(String labelName);

    /**
     * Get a value component string.<br>
     * Concrete implementation of this method may color
     * the value or even return as it is given as an argument.
     * @param value value of the form field.
     * @return a formatted string that gets displayed as a value of a field.
     */
    protected abstract String getValueString(String value);

    /**
     * Get a list representing a form to which the target player can input string data.
     * @param onPlayerSendString action which is invoked after the target player inputs a validated string.
     * @param validator predicate which determines if a given input string is valid.
     *                  When this method returns true, player's input is passed to {@code onPlayerSendString} consumer.
     * @param label string that labels this form
     * @param value string that gets displayed as current value.
     * @return a list of message parts representing an input form
     */
    protected final MessagePartsList getForm(Consumer<String> onPlayerSendString, Predicate<String> validator, String label, String value) {
        MessageParts formName = new MessageParts(this.getLabelString(label));

        if (value == null || value.isEmpty()) {
            value = this.messageConfig.getString(MessageConfigNodes.UI_FORM_NOTSET);
        }
        MessageParts formValue = new MessageParts(this.getValueString(value));

        MessageParts editButton = this.getButton(() -> {
            this.revokeAllRunnables();
            this.promptInput(label);
            this.getInputToForm(onPlayerSendString, validator);
        }, this.getEditButtonString());

        MessagePartsList form = new MessagePartsList();

        form.add(formName);
        form.add(formValue);
        form.add(editButton);
        form.addLine("");

        return form;
    }
}
