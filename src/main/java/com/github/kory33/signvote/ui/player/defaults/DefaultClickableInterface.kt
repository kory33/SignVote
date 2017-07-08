package com.github.kory33.signvote.ui.player.defaults

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.model.player.IPlayerClickableChatInterface
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.chatgui.util.collection.BijectiveHashMap
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

/**
 * An abstract class that provides default implementations for SignVote's clickable interfaces
 * @author Kory
 */
abstract class DefaultClickableInterface(
        override val targetPlayer: Player,
        override val runnableInvoker: RunnableInvoker,
        val messageConfig: JSONConfiguration)
        : IPlayerClickableChatInterface {

    override abstract val bodyMessages: MessagePartsList
    override val buttonIdMapping: BijectiveHashMap<MessageParts, Long> = BijectiveHashMap()

    override var isValidSession: Boolean = true

    /**
     * Get the header line of the interface
     * @return message component list representing the header
     */
    val interfaceHeader: MessagePartsList
        get() = MessagePartsList(this.messageConfig.getString(MessageConfigNodes.UI_HEADER))

    /**
     * Get the footer line of the interface
     * @return message component list representing the footer
     */
    val interfaceFooter: MessagePartsList
        get() = MessagePartsList(this.messageConfig.getString(MessageConfigNodes.UI_FOOTER))

    override fun constructInterfaceMessages(): MessagePartsList {
        val header = this.interfaceHeader
        val footer = this.interfaceFooter
        val messages = MessagePartsList()

        messages.addLine(header)
        messages.addLine(super.constructInterfaceMessages())
        messages.addAll(footer)

        return messages
    }
}
