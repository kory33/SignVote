package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.core.SignVote
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.*

/**
 * Executor class of "help"(default) sub-command
 * @author Kory
 */
class HelpCommandExecutor(plugin: SignVote, private val subCommandExecutorMap: Map<String, SubCommandExecutor>) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!

    override val helpString: String
        get() = ""

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        val messagePrefix = messageConfiguration.getString(MessageConfigNodes.MESSAGE_PREFIX)
        sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.COMMAND_HELP_HEADER))

        for ((subcommandName) in subCommandExecutorMap) {
            val commandHelp = messageConfiguration.getString("command.$subcommandName.summary")
            sender.sendMessage(messagePrefix + ChatColor.DARK_AQUA + subcommandName + ChatColor.GREEN + " - " + commandHelp)
        }

        return true
    }

}
