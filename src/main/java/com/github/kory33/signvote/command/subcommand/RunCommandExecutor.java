package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.core.SignVote;

public class RunCommandExecutor extends SubCommandExecutor {
    private final RunnableHashTable runnableHashTable;

    public RunCommandExecutor(SignVote plugin) {
        this.runnableHashTable = plugin.getRunnableHashTable();
    }

    @Override
    protected String getHelpString() {
        return "";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        try {
            long runnableId = Long.parseLong(args.remove(0));
            this.runnableHashTable.runSync(runnableId);
        } catch (IllegalArgumentException exception) {}

        return true;
    }
}
