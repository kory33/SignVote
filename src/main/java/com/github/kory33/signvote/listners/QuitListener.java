package com.github.kory33.signvote.listners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VotePointCreationSessionManager;

public class QuitListener implements Listener {
    private final VotePointCreationSessionManager votePointCreationSessionManager;

    public QuitListener(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.votePointCreationSessionManager = plugin.getVotePointCreationSessionManager();
    }

    public void deleteSessionOn(PlayerQuitEvent event) {
        Player quitPlayer = event.getPlayer();
        this.votePointCreationSessionManager.deleteSession(quitPlayer);
    }
}
