package com.github.kory33.signvote.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.command.subcommand.AddScoreCommandExecutor;
import com.github.kory33.signvote.command.subcommand.CreateCommandExecutor;
import com.github.kory33.signvote.command.subcommand.HelpCommandExecutor;
import com.github.kory33.signvote.command.subcommand.SubCommandExecutor;
import com.github.kory33.signvote.constants.SubCommands;
import com.github.kory33.signvote.core.SignVote;

public class SignVoteCommandExecutor implements CommandExecutor{
    private final CreateCommandExecutor createCommandExecutor;
    private final AddScoreCommandExecutor addScoreCommandExecutor;
    private final HelpCommandExecutor helpCommandExecutor;

    public SignVoteCommandExecutor(SignVote plugin) {
        this.createCommandExecutor = new CreateCommandExecutor(plugin);
        this.addScoreCommandExecutor = new AddScoreCommandExecutor(plugin);
        this.helpCommandExecutor = new HelpCommandExecutor(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

        String subCommand = argList.remove(0);
        SubCommandExecutor commandExecutor = null;
        switch(subCommand) {
            case SubCommands.CREATE:
                commandExecutor = this.createCommandExecutor;
                break;
            case SubCommands.ADD_SCORE:
                commandExecutor = this.addScoreCommandExecutor;
                break;
            default:
                commandExecutor = this.helpCommandExecutor;
        }

        boolean isSuccess = commandExecutor.onCommand(sender, command, argList);
        if (!isSuccess) {
            commandExecutor.displayHelp(sender);
        }
        return isSuccess;
    }
}
