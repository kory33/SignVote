package com.github.kory33.signvote.listners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kory33.signvote.api.event.PlayerAttemptToVoteEvent;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;

public class PlayerVoteListner implements Listener {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager voteSessionManager;

    public PlayerVoteListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.messageConfig = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerAttemptToVote(PlayerAttemptToVoteEvent event) {
        // TODO verify the vote and comfirm the vote
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractWithVotePoint(PlayerInteractEvent event) {
        // TODO send vote interface to the player
    }
}
