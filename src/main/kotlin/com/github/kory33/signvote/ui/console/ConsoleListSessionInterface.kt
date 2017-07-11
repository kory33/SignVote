package com.github.kory33.signvote.ui.console

import com.github.kory33.chatgui.model.IChatInterface
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.session.VoteSession
import com.github.ucchyocean.messaging.tellraw.MessageParts

/**
 * A class which represents a console interface(non-interactive)
 * which displays a list of existing sessions
 * @author Kory
 */
class ConsoleListSessionInterface(
        private val sessionManager: VoteSessionManager,
        private val messageConfig: JSONConfiguration) : IChatInterface {

    private fun getFormattedMessagePart(configurationNode: String, vararg objects: Any): MessageParts {
        return MessageParts(this.messageConfig.getFormatted(configurationNode, *objects))
    }

    private fun getSessionInfoLine(session: VoteSession): MessageParts {
        val openStateNode = if (session.isOpen) MessageConfigNodes.LIST_UI_SESSION_OPEN else MessageConfigNodes.LIST_UI_SESSION_OPEN
        val sessionState = messageConfig.getString(openStateNode)

        return getFormattedMessagePart(MessageConfigNodes.F_LIST_UI_ENTRY_TEMPLATE, session.name, sessionState)
    }

    override fun constructInterfaceMessages(): MessagePartsList {
        val header = this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER)
        val footer = this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER)

        val heading = this.getFormattedMessagePart(MessageConfigNodes.LIST_UI_HEADING)

        val messagePartsList = MessagePartsList()
        messagePartsList.addLine(header)
        messagePartsList.addLine(heading)

        this.sessionManager.voteSessionSet
                .map({ this.getSessionInfoLine(it) })
                .forEach({ messagePartsList.addLine(it) })

        messagePartsList.add(footer)

        return messagePartsList
    }
}
