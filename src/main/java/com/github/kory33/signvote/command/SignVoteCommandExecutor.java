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
import com.github.kory33.signvote.command.subcommand.HelpCommandExecutor;
import com.github.kory33.signvote.command.subcommand.OpenCommandExecutor;
import com.github.kory33.signvote.command.subcommand.SubCommandExecutor;
import com.github.kory33.signvote.constants.SubCommands;
import com.github.kory33.signvote.core.SignVote;

public class SignVoteCommandExecutor implements CommandExecutor{
    private final Map<String, SubCommandExecutor> subCommandExecutorMap;
    private final SubCommandExecutor defaultCommandExecutor;

    public SignVoteCommandExecutor(SignVote plugin) {
        HashMap<String, SubCommandExecutor> commandMaps = new HashMap<>();
        
        commandMaps.put(SubCommands.CREATE,    new CreateCommandExecutor(plugin));
        commandMaps.put(SubCommands.ADD_SCORE, new AddScoreCommandExecutor(plugin));
        commandMaps.put(SubCommands.OPEN,      new OpenCommandExecutor(plugin));
        commandMaps.put(SubCommands.CLOSE,     new CloseCommandExecutor(plugin));
        
        this.subCommandExecutorMap = Collections.unmodifiableMap(commandMaps);
        this.defaultCommandExecutor = new HelpCommandExecutor(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));
        
        SubCommandExecutor executor = null;
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
