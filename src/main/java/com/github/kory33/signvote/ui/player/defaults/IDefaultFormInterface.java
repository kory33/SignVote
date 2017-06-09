package com.github.kory33.signvote.ui.player.defaults;

import com.github.kory33.signvote.constants.MessageConfigNodes;

/**
 * Interface that provides default implementations for SignVote's form interfaces
 * @author Kory
 */
public interface IDefaultFormInterface extends IDefaultClickableInterface {
    default void notifyInvalidInput() {
        String message = this.getMessageConfig().getString(MessageConfigNodes.UI_FORM_INVALID_INPUT);
        this.getTargetPlayer().sendMessage(message);
    }

    default String getEditButtonString() {
        return this.getMessageConfig().getString(MessageConfigNodes.UI_FORM_EDIT_BUTTON);
    }

    default String getLabelString(String labelName) {
        return this.getMessageConfig().getFormatted(MessageConfigNodes.F_UI_FORM_LABEL, labelName);
    }

    default String getValueString(String value) {
        String displayedValue =
                (value == null || value.isEmpty()) ?
                        this.getMessageConfig().getString(MessageConfigNodes.UI_FORM_NOTSET) :
                        value;
        return this.getMessageConfig().getFormatted(MessageConfigNodes.F_UI_FORM_VALUE, displayedValue);
    }

    default void notifyInputCancellation() {
        this.getTargetPlayer().sendMessage(this.getMessageConfig().getString(MessageConfigNodes.UI_INPUT_CANCELLED));
    }

    default String getInputCancelButton() {
        return getMessageConfig().getString(MessageConfigNodes.UI_CANCEL_INPUT_BUTTON);
    }

    default String getFieldInputPromptMessage(String fieldName) {
        return this.getMessageConfig().getFormatted(MessageConfigNodes.F_UI_FORM_PROMPT, fieldName);
    }
}
