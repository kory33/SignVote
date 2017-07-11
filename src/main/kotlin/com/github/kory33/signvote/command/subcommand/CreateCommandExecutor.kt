package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.session.VoteSession
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import java.util.ArrayList
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Executor class of "create" sub-command
 * @author Kory
 */
class CreateCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!
    private val pluginLogger: Logger = plugin.logger

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.CREATE_SESSION)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (args.size < 1) {
            return false
        }

        val voteSessionName = args.removeAt(0)

        if (this.voteSessionManager.getVoteSession(voteSessionName) != null) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.SESSION_ALREADY_EXISTS))
            return true
        }

        try {
            this.voteSessionManager.addSession(VoteSession(voteSessionName))
        } catch (exception: Exception) {
            this.pluginLogger.log(Level.SEVERE, "Error while creating session: ", exception)
            return false
        }

        sender.sendMessage(messageConfiguration.getFormatted(MessageConfigNodes.F_SESSION_CREATED, voteSessionName))
        return true
    }

    override val helpString: String
        get() = messageConfiguration.getString(MessageConfigNodes.CREATE_COMMAND_HELP)
}
