package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Abstraction sub-command executor
 * @author Kory
 */
public interface SubCommandExecutor {
    /**
     * Get a string which includes command usage and description
     * @return string which includes command usage and description
     */
    String getHelpString();

    /**
     * This method is invoked when a player or console executes a command.
     * @param sender commandSender instance, which has run the command
     * @param command entire command being run
     * @param args list of arguments to be processed (sub-command argument is not included in this list)
     * @return a boolean value, false when the command usage has to be displayed, otherwise true
     */
    boolean onCommand(CommandSender sender, Command command, ArrayList<String> args);

    /**
     * Send help message to the target.
     * Without overrides, this method sends the string obtained from {@link SubCommandExecutor#getHelpString()}.
     * @param target target to which the help message is sent
     */
    default void displayHelp(CommandSender target) {
        target.sendMessage(this.getHelpString());
    }
}
