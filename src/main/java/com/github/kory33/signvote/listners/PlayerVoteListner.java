package com.github.kory33.signvote.listners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.PlayerChatInterface;
import com.github.kory33.signvote.ui.PlayerUnvoteInterface;
import com.github.kory33.signvote.ui.PlayerVoteInterface;

public class PlayerVoteListner implements Listener {
    private final VoteSessionManager voteSessionManager;
    private final JSONConfiguration messageConfig;

    public PlayerVoteListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.messageConfig = plugin.getMessagesConfiguration();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteractWithVotePoint(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        
        BlockState state = clickedBlock.getState();
        if (!(state instanceof Sign)) {
            return;
        }
        Sign sign = (Sign) state;

        VoteSession session = this.voteSessionManager.getVoteSession(sign);
        if (session == null) {
            return;
        }
        
        VotePoint votePoint = session.getVotePoint(sign);
        if (votePoint == null) {
            return;
        }
        
        Player clickPlayer = event.getPlayer();
        
        PlayerChatInterface chatInterface;
        if (session.getVoteManager().hasVoted(clickPlayer, votePoint)) {
            chatInterface = new PlayerUnvoteInterface(clickPlayer, session, votePoint, messageConfig);
        } else {
            chatInterface = new PlayerVoteInterface(clickPlayer, session, votePoint, messageConfig);
        }
        
        chatInterface.send();
        event.setCancelled(true);
        return;
    }
}
