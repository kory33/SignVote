package com.github.kory33.signvote.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.command.subcommand.AddScoreCommandExecutor;
import com.github.kory33.signvote.command.subcommand.CloseCommandExecutor;
import com.github.kory33.signvote.command.subcommand.CreateCommandExecutor;
import com.github.kory33.signvote.command.subcommand.DeleteCommandExecutor;
import com.github.kory33.signvote.command.subcommand.DeleteVPCommandExecutor;
import com.github.kory33.signvote.command.subcommand.HelpCommandExecutor;
import com.github.kory33.signvote.command.subcommand.ListCommandExecutor;
import com.github.kory33.signvote.command.subcommand.OpenCommandExecutor;
import com.github.kory33.signvote.command.subcommand.ReloadCommandExecutor;
import com.github.kory33.signvote.command.subcommand.RunCommandExecutor;
import com.github.kory33.signvote.command.subcommand.SaveCommandExecutor;
import com.github.kory33.signvote.command.subcommand.StatsCommandExecutor;
import com.github.kory33.signvote.command.subcommand.SubCommandExecutor;
import com.github.kory33.signvote.command.subcommand.UnvoteCommandExecutor;
import com.github.kory33.signvote.command.subcommand.VoteCommandExecutor;
import com.github.kory33.signvote.constants.SubCommands;
import com.github.kory33.signvote.core.SignVote;

/**
 * Executor of SignVote root command.
 *
 * This class acts as a proxy for all other specific "sub-commands"
 * such as "/signvote create" or "/signvote save".
 *
 * All the sub-commands should have been put into
 * "command map" in the constructor of this class.
 *
 * See the constructor's implementation for more details.
 */
public class SignVoteCommandExecutor implements CommandExecutor{
    private final Map<String, SubCommandExecutor> subCommandExecutorMap;
    private final SubCommandExecutor defaultCommandExecutor;

    /**
     * Constructs and automatically registers sub-commands
     * @param plugin instance of SignVote plugin
     */
    public SignVoteCommandExecutor(SignVote plugin) {
        HashMap<String, SubCommandExecutor> commandMaps = new HashMap<>();

        commandMaps.put(SubCommands.CREATE,    new CreateCommandExecutor(plugin));
        commandMaps.put(SubCommands.ADD_SCORE, new AddScoreCommandExecutor(plugin));
        commandMaps.put(SubCommands.LIST,      new ListCommandExecutor(plugin));
        commandMaps.put(SubCommands.OPEN,      new OpenCommandExecutor(plugin));
        commandMaps.put(SubCommands.CLOSE,     new CloseCommandExecutor(plugin));
        commandMaps.put(SubCommands.VOTE,      new VoteCommandExecutor(plugin));
        commandMaps.put(SubCommands.UNVOTE,    new UnvoteCommandExecutor(plugin));
        commandMaps.put(SubCommands.DELETEVP,  new DeleteVPCommandExecutor(plugin));
        commandMaps.put(SubCommands.DELETE,    new DeleteCommandExecutor(plugin));
        commandMaps.put(SubCommands.RELOAD,    new ReloadCommandExecutor(plugin));
        commandMaps.put(SubCommands.SAVE,      new SaveCommandExecutor(plugin));
        commandMaps.put(SubCommands.STATS,     new StatsCommandExecutor(plugin));

        this.defaultCommandExecutor = new HelpCommandExecutor(plugin, new HashMap<>(commandMaps));

        // Internal command should not be exposed to help command
        commandMaps.put(SubCommands.RUN,       new RunCommandExecutor(plugin));

        this.subCommandExecutorMap = Collections.unmodifiableMap(commandMaps);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

        SubCommandExecutor executor;
        if (args.length == 0) {
            executor = this.defaultCommandExecutor;
        } else {
            executor = this.subCommandExecutorMap.get(argList.remove(0));
        }

        if (executor == null) {
            executor = this.defaultCommandExecutor;
        }

        if (!executor.onCommand(sender, command, argList)) {
            executor.displayHelp(sender);
        }

        return true;
    }
}
