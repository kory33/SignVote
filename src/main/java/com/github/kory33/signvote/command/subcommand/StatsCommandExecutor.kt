package com.github.kory33.signvote.command.subcommand

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IPlayerClickableChatInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.constants.StatsType
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.ui.player.stats.StatsInterface
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Executor class of "stats" sub-command
 * @author Kory
 */
class StatsCommandExecutor(signVote: SignVote) : SubCommandExecutor {
    private val messageConfig: JSONConfiguration = signVote.messagesConfiguration!!
    private val sessionManager: VoteSessionManager = signVote.voteSessionManager!!
    private val runnableInvoker: RunnableInvoker = signVote.runnableInvoker!!
    private val interfaceManager: PlayerInteractiveInterfaceManager = signVote.interfaceManager!!

    override val helpString: String
        get() = this.messageConfig.getString(MessageConfigNodes.STATS_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.VIEW_STATS)) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (sender !is Player) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS))
            return true
        }

        if (args.size == 0) {
            return false
        }

        val session = this.sessionManager.getVoteSession(args.removeAt(0))
        if (session == null) {
            sender.sendMessage(this.messageConfig.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        val chatInterface: IPlayerClickableChatInterface?

        val statsType = if (args.size == 0) StatsType.MEAN.type else args.removeAt(0)

        var pageIndex: Int
        if (args.size == 0) {
            pageIndex = 0
        } else {
            try {
                pageIndex = Integer.parseInt(args.removeAt(0))
            } catch (exception: NumberFormatException) {
                pageIndex = 0
            }

        }

        chatInterface = StatsInterface.createNewInterface(sender, session, statsType, pageIndex, messageConfig,
                runnableInvoker, interfaceManager)

        if (chatInterface == null) {
            sender.sendMessage(messageConfig.getString(MessageConfigNodes.STATS_INVALID_TYPE))
            return true
        }

        chatInterface.send()
        this.interfaceManager.registerInterface(chatInterface)

        return true
    }
}
