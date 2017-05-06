package com.github.kory33.signvote.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.PlayerClickableChatInterface;
import com.github.kory33.signvote.ui.UnvoteInterface;
import com.github.kory33.signvote.ui.VoteInterface;

public class PlayerVoteListner implements Listener {
    private final VoteSessionManager voteSessionManager;
    private final JSONConfiguration messageConfig;
    private final RunnableHashTable runnableHashTable;
    private final PlayerInteractiveInterfaceManager interfaceManager;

    public PlayerVoteListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.messageConfig = plugin.getMessagesConfiguration();
        this.runnableHashTable = plugin.getRunnableHashTable();
        this.interfaceManager = plugin.getInterfaceManager();
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

        PlayerClickableChatInterface chatInterface;
        if (session.getVoteManager().hasVoted(clickPlayer.getUniqueId(), votePoint)) {
            chatInterface = new UnvoteInterface(clickPlayer, session, votePoint, messageConfig, runnableHashTable);
        } else {
            chatInterface = new VoteInterface(clickPlayer, session, votePoint, messageConfig, runnableHashTable);
        }

        this.interfaceManager.registerInterface(chatInterface);

        chatInterface.send();

        event.setCancelled(true);
        return;
    }
}
