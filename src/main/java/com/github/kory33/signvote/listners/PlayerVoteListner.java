package com.github.kory33.signvote.listners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kory33.signvote.core.SignVote;

public class PlayerVoteListner implements Listener {
    public PlayerVoteListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteractWithVotePoint(PlayerInteractEvent event) {
        // TODO display the voting interfaces in the players chat using tellraw system
    }
}
