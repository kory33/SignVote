package com.github.kory33.signvote.listeners

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IPlayerClickableChatInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.ui.player.UnvoteInterface
import com.github.kory33.signvote.ui.player.VoteInterface
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * A Listener implementation which listens to player's attempts to vote to a vote point
 */
class PlayerVoteListener(private val plugin: SignVote) : Listener {
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!
    private val messageConfig: JSONConfiguration = plugin.messagesConfiguration!!
    private val runnableInvoker: RunnableInvoker = plugin.runnableInvoker!!
    private val interfaceManager: PlayerInteractiveInterfaceManager = plugin.interfaceManager!!

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteractWithVotePoint(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock
        if (!this.plugin.api!!.isSignVoteSign(clickedBlock)) {
            return
        }

        val sign = clickedBlock.state as Sign

        val session = this.voteSessionManager.getVoteSession(sign)
        val votePoint = session!!.getVotePoint(sign)!!

        val clickPlayer = event.player
        val chatInterface: IPlayerClickableChatInterface
        if (session.voteManager.hasVoted(clickPlayer.uniqueId, votePoint)) {
            chatInterface = UnvoteInterface(clickPlayer, session, votePoint, messageConfig, runnableInvoker)
        } else {
            chatInterface = VoteInterface(clickPlayer, session, votePoint, messageConfig, runnableInvoker)
        }

        this.interfaceManager.registerInterface(chatInterface)

        chatInterface.send()

        event.isCancelled = true
    }
}
