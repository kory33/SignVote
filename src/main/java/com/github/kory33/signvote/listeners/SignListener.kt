package com.github.kory33.signvote.listeners

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.Patterns
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.constants.SignTexts
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.vote.VotePoint
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent

/**
 * A Listener implementation which listens to player's interaction with sign.
 */
class SignListener(private val plugin: SignVote) : Listener {
    private val messageConfig: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun rejectSignCreation(sign: SignChangeEvent, reason: String) {
        for (index in 0..3) {
            sign.setLine(index, "")
        }

        sign.setLine(1, SignTexts.SIGN_CREATION_REJECTED_TEXT)
        sign.player.sendMessage(reason)
    }

    @EventHandler
    fun onVoteSignCreated(sign: SignChangeEvent) {
        if (sign.getLine(0) != SignTexts.SIGN_CREATION_TEXT) {
            return
        }

        if (!sign.player.hasPermission(PermissionNodes.CREATE_SIGN)) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.MISSING_PERMS))
            return
        }

        val sessionName = sign.getLine(1)
        val session = this.voteSessionManager.getVoteSession(sessionName)

        if (session == null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return
        }

        val pointName = sign.getLine(2)
        if (!Patterns.PATTERN_VALID_VP_NAME.matcher(pointName).matches()) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.VOTEPOINT_NAME_INVALID))
            return
        }

        if (session.getVotePoint(pointName) != null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.VOTEPOINT_ALREADY_EXISTS))
            return
        }

        val votePoint = VotePoint(pointName, sign.block.state as Sign)
        session.addVotePoint(votePoint)

        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT)

        sign.player.sendMessage(messageConfig.getFormatted(MessageConfigNodes.VOTEPOINT_CREATED, sessionName, votePoint.name))
    }

    @EventHandler
    fun onVotePointBreak(event: BlockBreakEvent) {
        val brokenBlock = event.block
        if (!this.plugin.api!!.isSignVoteSign(brokenBlock)) {
            return
        }

        val sign = brokenBlock.state as Sign
        val session = this.voteSessionManager.getVoteSession(sign)

        val sessionName = session!!.name
        val votepointName = session.getVotePoint(sign)!!.name

        event.isCancelled = true
        event.player.sendMessage(messageConfig.getFormatted(MessageConfigNodes.F_VOTEPOINT_BREAK,
                sessionName, votepointName))
    }
}
