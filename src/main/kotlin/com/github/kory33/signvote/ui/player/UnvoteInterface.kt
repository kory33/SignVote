package com.github.kory33.signvote.ui.player

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.exception.VotePointNotVotedException
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.defaults.DefaultClickableInterface
import com.github.kory33.signvote.vote.VotePoint
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

/**
 * Represents an interface which confirms and executes player's un-vote.
 * @author Kory
 */
class UnvoteInterface(targetPlayer: Player,
                      private val session: VoteSession,
                      private val votePoint: VotePoint,
                      messageConfig: JSONConfiguration, runnableInvoker: RunnableInvoker)
    : DefaultClickableInterface(targetPlayer, runnableInvoker, messageConfig) {

    private fun unVote() {
        if (!this.isValidSession) {
            return
        }
        try {
            this.session.voteManager.removeVote(this.targetPlayer.uniqueId, votePoint)
            targetPlayer.sendMessage(
                    this.messageConfig.getFormatted(MessageConfigNodes.F_UNVOTED, this.votePoint.name))
        } catch (e: VotePointNotVotedException) {
            this.targetPlayer.sendMessage(this.messageConfig.getString(MessageConfigNodes.NOT_VOTED))
        }

        this.revokeSession()
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

    private val heading: String
        get() {
            val votedScore =
                    this.session.voteManager.getVotedScore(this.targetPlayer.uniqueId, this.votePoint)
                            ?: throw IllegalStateException("Player Unvote Interface has been invoked against a non-voted votepoint!")

            return messageConfig.getFormatted(MessageConfigNodes.UNVOTE_UI_HEADING, this.votePoint.name, votedScore)
        }

    private val sessionClosedMessage: MessagePartsList
        get() {
            val message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED)
            return MessagePartsList(message + "\n")
        }

    private fun cancelAction() {
        super.cancelAction(this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED))
    }

    override val bodyMessages: MessagePartsList
        get() {
            if (!this.session.isOpen) {
                return this.sessionClosedMessage
            }

            val defaultButtonMessage = this.messageConfig.getString(MessageConfigNodes.UI_BUTTON)

            val messagePartsList = MessagePartsList()
            messagePartsList.addLine(this.heading)

            messagePartsList.add(this.getButton({ this.unVote() }, defaultButtonMessage))
            messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UNVOTE_UI_COMFIRM))

            messagePartsList.add(this.getButton({ this.cancelAction() }, defaultButtonMessage))
            messagePartsList.add(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL))

            return messagePartsList
        }
}
