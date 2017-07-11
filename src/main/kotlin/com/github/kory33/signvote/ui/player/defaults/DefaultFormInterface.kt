package com.github.kory33.signvote.ui.player.defaults

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.listener.PlayerChatInterceptor
import com.github.kory33.chatgui.model.player.IFormChatInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

/**
 * Interface that provides default implementations for SignVote's form interfaces
 * @author Kory
 */
abstract class DefaultFormInterface(
            player: Player,
            runnableInvoker: RunnableInvoker,
            messageConfig: JSONConfiguration,
            override val chatInterceptor: PlayerChatInterceptor
        ) : IFormChatInterface, DefaultClickableInterface(player, runnableInvoker, messageConfig) {

    override var inputCancelButton: MessageParts? = null

    override fun notifyInvalidInput() {
        val message = this.messageConfig.getString(MessageConfigNodes.UI_FORM_INVALID_INPUT)
        this.targetPlayer.sendMessage(message)
    }

    override val editButtonString: String
        get() = this.messageConfig.getString(MessageConfigNodes.UI_FORM_EDIT_BUTTON)

    override fun getLabelString(labelName: String): String {
        return this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_LABEL, labelName)
    }

    override fun getValueString(value: String): String {
        val displayedValue = if (value.isEmpty())
            this.messageConfig.getString(MessageConfigNodes.UI_FORM_NOTSET)
        else
            value
        return this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_VALUE, displayedValue)
    }

    override fun notifyInputCancellation() {
        this.targetPlayer.sendMessage(this.messageConfig.getString(MessageConfigNodes.UI_INPUT_CANCELLED))
    }

    override fun getInputCancelButton(): String {
        return messageConfig.getString(MessageConfigNodes.UI_CANCEL_INPUT_BUTTON)
    }

    override fun getFieldInputPromptMessage(fieldName: String): String {
        return this.messageConfig.getFormatted(MessageConfigNodes.F_UI_FORM_PROMPT, fieldName)
    }
}
