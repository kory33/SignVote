package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitScheduler

import java.util.ArrayList
import java.util.concurrent.CompletableFuture

/**
 * Executor class of "save" sub-command
 * @author Kory
 */
class SaveCommandExecutor(private val plugin: SignVote) : SubCommandExecutor {
    private val scheduler: BukkitScheduler = plugin.server.scheduler
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!

    override val helpString: String
        get() = ""

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (!sender.hasPermission(PermissionNodes.SAVE)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
        }

        CompletableFuture.runAsync { this.plugin.saveSessionData() }.thenRun {
            scheduler.scheduleSyncDelayedTask(this.plugin
            ) { sender.sendMessage(messageConfiguration.getString(MessageConfigNodes.SAVE_COMPLETE)) }
        }

        return true
    }
}
