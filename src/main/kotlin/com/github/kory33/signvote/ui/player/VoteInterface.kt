package com.github.kory33.signvote.ui.player

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.model.player.IPlayerClickableChatInterface
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.exception.InvalidScoreVotedException
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException
import com.github.kory33.signvote.exception.VoteSessionClosedException
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.defaults.DefaultClickableInterface
import com.github.kory33.signvote.vote.Limit
import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

import java.util.Comparator
import java.util.function.Function

/**
 * Represents an interface which allows a player to vote to a vote point.
 * @author Kory
 */
class VoteInterface(player: Player, private val session: VoteSession, private val votePoint: VotePoint,
                    messageConfig: JSONConfiguration, runnableInvoker: RunnableInvoker)
    : IPlayerClickableChatInterface, DefaultClickableInterface(player, runnableInvoker, messageConfig) {

    private val heading: MessageParts
        get() {
            val message = messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_HEADING,
                    this.votePoint.name)
            return MessageParts(message)
        }

    private fun vote(voteScore: VoteScore) {
        if (!this.isValidSession) {
            return
        }

        var resultMessage: String

        try {
            this.session.vote(this.targetPlayer, votePoint, voteScore)
            resultMessage = this.messageConfig.getFormatted(MessageConfigNodes.VOTED)
        } catch (exception: ScoreCountLimitReachedException) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.REACHED_VOTE_SCORE_LIMIT)
        } catch (exception: VotePointAlreadyVotedException) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.VOTEPOINT_ALREADY_VOTED)
        } catch (exception: InvalidScoreVotedException) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.INVALID_VOTE_SCORE)
        } catch (e: VoteSessionClosedException) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED)
        }

        this.targetPlayer.sendMessage(resultMessage)

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

    private fun cancelAction() {
        super<DefaultClickableInterface>.cancelAction(this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED))
    }

    private fun getScoreSelectionLine(score: VoteScore, remaining: Limit): String {
        val remainingString = if (remaining.isInfinite)
            this.messageConfig.getString(MessageConfigNodes.INFINITE)
        else
            remaining.toString()

        return this.messageConfig
                .getFormatted(MessageConfigNodes.VOTE_UI_SCORE_SELECTION, score.toInt(), remainingString)
    }

    private val sessionClosedMessage: MessagePartsList
        get() {
            val message = this.messageConfig.getString(MessageConfigNodes.VOTE_UI_NONE_AVAILABLE)
            val messagePartsList = MessagePartsList()
            messagePartsList.addLine(message)
            return messagePartsList
        }

    override val bodyMessages: MessagePartsList
        get() {
            val availableVotePoints = this.session.getAvailableVoteCounts(this.targetPlayer)
            if (availableVotePoints.isEmpty()) {
                return this.sessionClosedMessage
            }

            if (!this.session.isOpen) {
                val message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED)
                val messagePartsList = MessagePartsList(message)
                messagePartsList.addLine("")
                return messagePartsList
            }

            val defaultButtonMessage = this.messageConfig.getString(MessageConfigNodes.UI_BUTTON)

            val messagePartsList = MessagePartsList()
            messagePartsList.addLine(this.heading)

            availableVotePoints
                    .entries
                    .stream()
                    .sorted(Comparator.comparing(Function { it.value }, Comparator.reverseOrder<Limit>()))
                    .forEach { entry ->
                        val score = entry.key
                        val limit = entry.value

                        messagePartsList.add(this.getButton({ this.vote(score) }, defaultButtonMessage))
                        messagePartsList.addLine(this.getScoreSelectionLine(score, limit))
                    }

            messagePartsList.add(this.getButton({ this.cancelAction() }, defaultButtonMessage))
            messagePartsList.add(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL))

            return messagePartsList
        }
}
