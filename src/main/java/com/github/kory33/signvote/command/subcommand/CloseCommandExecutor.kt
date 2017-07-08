package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.*

/**
 * Executor class of "close" sub-command
 * @author Kory
 */
class CloseCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!

    override val helpString: String
        get() = this.messageConfiguration.getString(MessageConfigNodes.CLOSE_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.CLOSE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (args.size < 1) {
            return false
        }

        val closeTargetSessionName = args.removeAt(0)
        val targetVoteSession = this.voteSessionManager.getVoteSession(closeTargetSessionName)

        if (targetVoteSession == null) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        if (!targetVoteSession.isOpen) {
            sender.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_ALREADY_CLOSED, closeTargetSessionName))
            return true
        }

        targetVoteSession.isOpen = false
        val completionMessage = this.messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_CLOSED, closeTargetSessionName)
        sender.sendMessage(completionMessage)
        return true
    }
}
