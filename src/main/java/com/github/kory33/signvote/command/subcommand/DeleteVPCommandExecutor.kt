package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.vote.VotePoint
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import java.util.ArrayList

/**
 * Executor class of "deletevp" sub-command
 * @author Kory
 */
class DeleteVPCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!

    override val helpString: String
        get() = this.messageConfiguration.getString(MessageConfigNodes.DELETE_VP_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (args.size < 2) {
            return false
        }

        if (!sender.hasPermission(PermissionNodes.DELETE_VOTEPOINT)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
        }

        val sessionName = args.removeAt(0)
        val votepointName = args.removeAt(0)

        val session = this.voteSessionManager.getVoteSession(sessionName)
        if (session == null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        val votePoint = session.getVotePoint(votepointName)
        if (votePoint == null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_DOES_NOT_EXIST))
            return true
        }

        session.deleteVotepoint(votePoint)
        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigNodes.F_VOTEPOINT_DELETED, sessionName,
                votepointName))

        return true
    }

}
