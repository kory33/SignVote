package com.github.kory33.signvote.command.subcommand

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.ui.console.ConsoleListSessionInterface
import com.github.kory33.signvote.ui.player.ListSessionInterface
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Executor class of "list" sub-command
 * @author Kory
 */
class ListCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfig: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!
    private val runnableInvoker: RunnableInvoker = plugin.runnableInvoker!!
    private val interfaceManager: PlayerInteractiveInterfaceManager = plugin.interfaceManager!!

    override val helpString: String
        get() = messageConfig.getString(MessageConfigNodes.LIST_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.LIST_SESSION)) {
            sender.sendMessage(messageConfig.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (sender !is Player) {
            val listInterface = ConsoleListSessionInterface(voteSessionManager, messageConfig)
            listInterface.send(sender)
            return true
        }

        val pageIndexString = if (args.size != 0) args.removeAt(0) else "0"
        val pageIndex = if (NumberUtils.isNumber(pageIndexString)) NumberUtils.createInteger(pageIndexString) else 0

        val listInterface = ListSessionInterface(sender, voteSessionManager, messageConfig, runnableInvoker, interfaceManager, pageIndex)
        interfaceManager.registerInterface(listInterface)
        listInterface.send()

        return true
    }
}
