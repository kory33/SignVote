package com.github.kory33.signvote.ui.player

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IBrowseablePageInterface
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.defaults.DefaultBrowseableInterface
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

import java.util.ArrayList
import java.util.function.Consumer

/**
 * Represents an interface which displays a list of existing sessions
 * @author Kory
 */
class ListSessionInterface
    : IBrowseablePageInterface, DefaultBrowseableInterface {

    private val voteSessionManager: VoteSessionManager
    override val heading: MessagePartsList
            = MessagePartsList(messageConfig.getString(MessageConfigNodes.LIST_UI_HEADING) + "\n")

    constructor(player: Player,
                voteSessionManager: VoteSessionManager,
                messageConfig: JSONConfiguration,
                runnableInvoker: RunnableInvoker,
                interfaceManager: PlayerInteractiveInterfaceManager,
                pageIndex: Int) : super(player, runnableInvoker, messageConfig, interfaceManager, pageIndex){

        this.voteSessionManager = voteSessionManager
    }

    private constructor(oldInterface: ListSessionInterface, newIndex: Int) : super(oldInterface, newIndex) {

        this.voteSessionManager = oldInterface.voteSessionManager
    }

    /**
     * Get a message formatted with the given array of Object arguments(optional)
     * @param configurationNode configuration node from which the message should be fetched
     * *
     * @param objects objects used in formatting the fetched string
     * *
     * @return formatted message component
     */
    private fun getFormattedMessagePart(configurationNode: String, vararg objects: Any): MessageParts {
        return MessageParts(this.messageConfig.getFormatted(configurationNode, *objects))
    }

    private fun getEntry(session: VoteSession): MessagePartsList {
        val openStateNode = if (session.isOpen) MessageConfigNodes.LIST_UI_SESSION_OPEN else MessageConfigNodes.LIST_UI_SESSION_CLOSED
        val sessionState = messageConfig.getString(openStateNode)
        return MessagePartsList(getFormattedMessagePart(MessageConfigNodes.F_LIST_UI_ENTRY_TEMPLATE, session.name, sessionState))
    }

    override val entryList: ArrayList<MessagePartsList>
        get() {
            val entryList = ArrayList<MessagePartsList>()
            this.voteSessionManager.voteSessionSet
                    .map({ this.getEntry(it) })
                    .forEach(Consumer<MessagePartsList> { entryList.add(it) })
            return entryList
        }

    override fun yieldPage(pageIndex: Int): IBrowseablePageInterface {
        return ListSessionInterface(this, pageIndex)
    }
}
