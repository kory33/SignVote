package com.github.kory33.signvote.command.subcommand

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.listener.PlayerChatInterceptor
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.exception.data.InvalidLimitDataException
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.AddScoreInterface
import com.github.kory33.signvote.vote.Limit
import com.github.kory33.signvote.vote.VoteLimit
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Executor class of "addscore" sub-command
 * @author Kory
 */
class AddScoreCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!
    private val runnableInvoker: RunnableInvoker = plugin.runnableInvoker!!
    private val interfaceManager: PlayerInteractiveInterfaceManager = plugin.interfaceManager!!
    private val chatInterceptor: PlayerChatInterceptor = plugin.chatInterceptor!!

    override val helpString: String
        get() = messageConfiguration.getString(MessageConfigNodes.ADD_SCORE_COMMAND_HELP)

    /**
     * Construct and send addscore interface to the player
     * which adds a vote limit to the given session
     * @param player target player
     * *
     * @param session target vote session
     */
    private fun sendAddScoreInterface(player: Player, session: VoteSession) {
        val chatInterface = AddScoreInterface(player, session, messageConfiguration,
                runnableInvoker, chatInterceptor)
        this.interfaceManager.registerInterface(chatInterface)
        chatInterface.send()
    }

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.MODIFY_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (args.size == 0) {
            return false
        }

        val voteSessionname = args.removeAt(0)
        val session = this.voteSessionManager.getVoteSession(voteSessionname)

        if (session == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        if (args.size < 2 && sender is Player) {
            this.sendAddScoreInterface(sender, session)
            return true
        } else if (args.size < 2) {
            return false
        }

        val voteScore: VoteScore
        val limit: Limit
        try {
            voteScore = VoteScore(Integer.parseInt(args.removeAt(0)))
            limit = Limit.fromString(args.removeAt(0))
        } catch (e: NumberFormatException) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_NUMBER))
            return true
        } catch (e: InvalidLimitDataException) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_NUMBER))
            return true
        }

        var permission = PermissionNodes.VOTE
        if (!args.isEmpty()) {
            permission = args.removeAt(0)
            if (permission.equals("op", ignoreCase = true)) {
                permission = PermissionNodes.VOTE_MORE
            }
        }

        try {
            session.voteLimitManager.addVoteLimit(VoteLimit(voteScore, limit, permission))
        } catch (e: IllegalArgumentException) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_NUMBER))
            return true
        }

        val limitString = if (limit.isInfinite)
            messageConfiguration.getString(MessageConfigNodes.INFINITE)
        else
            limit.toString()

        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigNodes.F_SCORE_LIMIT_ADDED,
                limitString, voteScore.toInt(), session.name, permission))
        return true
    }
}
