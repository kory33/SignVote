package com.github.kory33.signvote.ui.player

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.listener.PlayerChatInterceptor
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.defaults.DefaultFormInterface
import com.github.kory33.signvote.vote.Limit
import com.github.kory33.signvote.vote.VoteLimit
import com.github.kory33.signvote.vote.VoteScore
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.entity.Player

/**
 * Represents an interface that allows the player to add a new vote score limit.
 * @author Kory
 */
class AddScoreInterface(player: Player,
                        private val session: VoteSession, messageConfig: JSONConfiguration,
                        runnableInvoker: RunnableInvoker,
                        chatInterceptor: PlayerChatInterceptor)
    : DefaultFormInterface(player, runnableInvoker, messageConfig, chatInterceptor) {
    private var score: Int? = null
    private var limitInteger: Int? = null
    private var permission: String? = null

    private val voteLimitString: String
        get() {
            if (this.limitInteger == null) {
                return this.messageConfig.getString(MessageConfigNodes.INFINITE)
            }

            return this.limitInteger!!.toString()
        }

    private fun addScoreLimit() {
        if (score == null) {
            targetPlayer.sendMessage(messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_SCORE_NOT_SET))
            return
        }

        val convertedPermission: String
        if (permission == null || permission!!.isEmpty()) {
            convertedPermission = PermissionNodes.VOTE
        } else if (permission == "op") {
            convertedPermission = PermissionNodes.VOTE_MORE
        } else {
            convertedPermission = permission!!
        }


        val voteScore = VoteScore(score!!)
        val limit = Limit(this.limitInteger)
        val voteLimit = VoteLimit(voteScore, limit, convertedPermission)

        this.session.voteLimitManager.addVoteLimit(voteLimit)

        val limitString = if (limit.isInfinite)
            messageConfig.getString(MessageConfigNodes.INFINITE)
        else
            limit.toString()

        targetPlayer.sendMessage(messageConfig.getFormatted(MessageConfigNodes.F_SCORE_LIMIT_ADDED,
                limitString, score.toString(), session.name, convertedPermission))

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

    private val heading: MessageParts
        get() = this.getFormattedMessagePart(MessageConfigNodes.ADDSCORE_UI_HEADING, this.session.name)

    private fun validateLimitInput(input: String): Boolean {
        try {
            val limit = NumberUtils.createInteger(input)!!
            return limit > 0
        } catch (exception: NumberFormatException) {
            return false
        }

    }

    override val bodyMessages: MessagePartsList
        get() {
            val scoreForm = super.getForm(
                    { this.score = NumberUtils.createInteger(it) },
                    { NumberUtils.isNumber(it) },
                    this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_SCORE),
                    if (score == null) "null" else score!!.toString()
            )

            val limitForm = super.getForm(
                    { this.limitInteger = NumberUtils.createInteger(it) },
                    { this.validateLimitInput(it) },
                    this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_LIMIT),
                    this.voteLimitString
            )

            val permissionForm = super.getForm(
                    { this.permission = it },
                    { true },
                    this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_PERMISSION),
                    if (this.permission == null) "null" else this.permission!!
            )

            val submitButton = this.getButton({ this.addScoreLimit() },
                    this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_SUBMIT))

            val messagePartsList = MessagePartsList()

            messagePartsList.addLine(this.heading)
            messagePartsList.addAll(scoreForm)
            messagePartsList.addAll(limitForm)
            messagePartsList.addAll(permissionForm)
            messagePartsList.add(submitButton)

            return messagePartsList
        }
}
