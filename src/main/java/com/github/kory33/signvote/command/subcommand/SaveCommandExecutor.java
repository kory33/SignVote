package com.github.kory33.signvote.command.subcommand;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.core.SignVote;

public class SaveCommandExecutor extends SubCommandExecutor {
    private final SignVote plugin;
    private final JSONConfiguration messageConfiguration;
    
    public SaveCommandExecutor(SignVote plugin) {
        this.plugin = plugin;
        this.messageConfiguration = plugin.getMessagesConfiguration();
    }

    @Override
    protected String getHelpString() {
        return "";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, ArrayList<String> args) {
        if (!sender.hasPermission(PermissionNodes.SAVE)) {
            sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.MISSING_PERMS));
        }
        
        CompletableFuture.runAsync(this.plugin::saveSessionData).thenRun(() -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                sender.sendMessage(messageConfiguration.getString(MessageConfigurationNodes.SAVE_COMPLETE));
            });
        });
        
        return true;
    }
}