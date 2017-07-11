package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.exception.VotePointNotVotedException
import com.github.kory33.signvote.manager.VoteSessionManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Executor class of "unvote" sub-command
 * @author Kory
 */
class UnvoteCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!

    override val helpString: String
        get() = this.messageConfiguration.getString(MessageConfigNodes.UNVOTE_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS))
            return true
        }

        if (!sender.hasPermission(PermissionNodes.UNVOTE)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (args.size < 2) {
            return false
        }

        val player = sender
        val sessionName = args.removeAt(0)
        val votePointName = args.removeAt(0)

        val session = this.voteSessionManager.getVoteSession(sessionName)
        if (session == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        val votePoint = session.getVotePoint(votePointName)
        if (votePoint == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_DOES_NOT_EXIST))
            return true
        }

        if (!session.isOpen) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTE_SESSION_CLOSED))
            return true
        }

        try {
            session.voteManager.removeVote(player.uniqueId, votePoint)
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.F_UNVOTED, votePointName))
        } catch (exception: VotePointNotVotedException) {
            player.sendMessage(this.messageConfiguration.getFormatted(MessageConfigNodes.NOT_VOTED, votePointName))
        }

        return true
    }

}
