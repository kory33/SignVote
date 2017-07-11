package com.github.kory33.signvote.command.subcommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import java.util.ArrayList

/**
 * Abstraction sub-command executor
 * @author Kory
 */
interface SubCommandExecutor {
    /**
     * Get a string which includes command usage and description
     * @return string which includes command usage and description
     */
    val helpString: String

    /**
     * This method is invoked when a player or console executes a command.
     * @param sender commandSender instance, which has run the command
     * *
     * @param command entire command being run
     * *
     * @param args list of arguments to be processed (sub-command argument is not included in this list)
     * *
     * @return a boolean value, false when the command usage has to be displayed, otherwise true
     */
    fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean

    /**
     * Send help message to the target.
     * Without overrides, this method sends the string obtained from [SubCommandExecutor.helpString].
     * @param target target to which the help message is sent
     */
    fun displayHelp(target: CommandSender) {
        target.sendMessage(this.helpString)
    }
}
