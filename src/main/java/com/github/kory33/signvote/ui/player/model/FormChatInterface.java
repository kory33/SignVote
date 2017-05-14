package com.github.kory33.signvote.ui.player.model;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableInvoker;
import com.github.kory33.signvote.listeners.PlayerChatInterceptor;
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

    private MessageParts inputCancelButton;

    public FormChatInterface(Player player, RunnableInvoker runnableInvoker, PlayerChatInterceptor chatInterceptor) {
        super(player, runnableInvoker);
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
        this.inputCancelButton = null;
    }

    private MessageParts getCancelInputButton() {
        this.inputCancelButton = super.getButton(() -> {
            this.chatInterceptor.cancelAnyInterception(this.targetPlayer, "Input cancelled.");
            super.revokeButton(this.inputCancelButton);

            // send footer
            (new MessageComponent(this.getInterfaceFooter())).send(this.targetPlayer);

            // notify cancellation
            this.notifyInputCancellation();

            // re-send the interface
            this.send();
        }, this.getInputCancelButton());
        return this.inputCancelButton;
    }

    private void promptInput(String fieldName) {
        String message = this.getFieldInputPromptMessage(fieldName);
        MessageParts cancelInputButton = this.getCancelInputButton();

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.add(message);
        messagePartsList.add(" ");
        messagePartsList.add(cancelInputButton);

        (new MessageComponent(messagePartsList)).send(this.targetPlayer);
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
                    super.revokeButton(this.inputCancelButton);
                    this.send();
                })
                .exceptionally((error) -> null);
    }

    /**
     * Notify the player that the input value is invalid,
     * asking the input to be attempted again.
     */
    protected abstract void notifyInvalidInput();

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
     * Get a value component string.
     * <p>
     * Concrete implementation of this method may color
     * the value or even return as it is given as an argument.
     * <p>
     * When <code>value</code> argument is null,
     * this method should return a string indicating that no value is set to the field.
     * @param value value of the form field.
     * @return a formatted string that gets displayed as a value of a field.
     * "Not set" state should be indicated when null.
     */
    protected abstract String getValueString(String value);

    /**
     * Notify the player that the field input prompt has been cancelled.
     */
    protected abstract void notifyInputCancellation();

    /**
     * Get a string that represents a button to cancel the input prompt
     * @return a button string
     */
    protected abstract String getInputCancelButton();

    /**
     * Get a message that prompts the player to input a value to the field.
     * @param fieldName name of the field.
     * @return message that prompts the player to input a value to the field.
     */
    protected abstract String getFieldInputPromptMessage(String fieldName);

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
