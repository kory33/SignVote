package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import java.util.ArrayList

/**
 * Executor class of "reload" sub-command
 * @author Kory
 */
class ReloadCommandExecutor(private val plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!

    override val helpString: String
        get() = ""

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.RELOAD)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        this.plugin.reload()
        sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.RELOAD_COMPLETE))
        return true
    }
}
