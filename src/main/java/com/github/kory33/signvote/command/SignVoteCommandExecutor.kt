package com.github.kory33.signvote.command

import com.github.kory33.signvote.command.subcommand.*
import com.github.kory33.signvote.constants.SubCommands
import com.github.kory33.signvote.core.SignVote
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

import java.util.*

/**
 * Executor of SignVote root command.

 * This class acts as a proxy for all other specific "sub-commands"
 * such as "/signvote create" or "/signvote save".

 * All the sub-commands should have been put into
 * "command map" in the constructor of this class.

 * See the constructor's implementation for more details.
 */
class SignVoteCommandExecutor
/**
 * Constructs and automatically registers sub-commands
 * @param plugin instance of SignVote plugin
 */
(plugin: SignVote) : CommandExecutor {
    private val subCommandExecutorMap: Map<String, SubCommandExecutor>
    private val defaultCommandExecutor: SubCommandExecutor

    init {
        val commandMaps = HashMap<String, SubCommandExecutor>()

        commandMaps.put(SubCommands.CREATE, CreateCommandExecutor(plugin))
        commandMaps.put(SubCommands.ADD_SCORE, AddScoreCommandExecutor(plugin))
        commandMaps.put(SubCommands.LIST, ListCommandExecutor(plugin))
        commandMaps.put(SubCommands.OPEN, OpenCommandExecutor(plugin))
        commandMaps.put(SubCommands.CLOSE, CloseCommandExecutor(plugin))
        commandMaps.put(SubCommands.VOTE, VoteCommandExecutor(plugin))
        commandMaps.put(SubCommands.UNVOTE, UnvoteCommandExecutor(plugin))
        commandMaps.put(SubCommands.DELETEVP, DeleteVPCommandExecutor(plugin))
        commandMaps.put(SubCommands.DELETE, DeleteCommandExecutor(plugin))
        commandMaps.put(SubCommands.RELOAD, ReloadCommandExecutor(plugin))
        commandMaps.put(SubCommands.SAVE, SaveCommandExecutor(plugin))
        commandMaps.put(SubCommands.STATS, StatsCommandExecutor(plugin))

        this.defaultCommandExecutor = HelpCommandExecutor(plugin, HashMap(commandMaps))

        this.subCommandExecutorMap = Collections.unmodifiableMap(commandMaps)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val argList = ArrayList(Arrays.asList(*args))

        var executor: SubCommandExecutor?
        if (args.isEmpty()) {
            executor = this.defaultCommandExecutor
        } else {
            executor = this.subCommandExecutorMap[argList.removeAt(0)]
        }

        if (executor == null) {
            executor = this.defaultCommandExecutor
        }

        if (!executor.onCommand(sender, command, argList)) {
            executor.displayHelp(sender)
        }

        return true
    }
}
