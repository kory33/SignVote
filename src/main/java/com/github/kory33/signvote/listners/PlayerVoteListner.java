package com.github.kory33.signvote.listners;

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
import com.github.kory33.signvote.ui.PlayerInteractiveChatInterface;
import com.github.kory33.signvote.ui.PlayerUnvoteInterface;
import com.github.kory33.signvote.ui.PlayerVoteInterface;

public class PlayerVoteListner implements Listener {
    private final VoteSessionManager voteSessionManager;
    private final JSONConfiguration messageConfig;
    private final RunnableHashTable runnableHashTable;
    private final PlayerInteractiveInterfaceManager interfaceManager;
    private final PlayerChatInterceptor chatInterceptor;

    public PlayerVoteListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.messageConfig = plugin.getMessagesConfiguration();
        this.runnableHashTable = plugin.getRunnableHashTable();
        this.interfaceManager = plugin.getInterfaceManager();
        this.chatInterceptor = plugin.getChatInterceptor();
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

        PlayerInteractiveChatInterface chatInterface;
        if (session.getVoteManager().hasVoted(clickPlayer.getUniqueId(), votePoint)) {
            chatInterface = new PlayerUnvoteInterface(clickPlayer, session, votePoint, messageConfig, runnableHashTable, chatInterceptor);
        } else {
            chatInterface = new PlayerVoteInterface(clickPlayer, session, votePoint, messageConfig, runnableHashTable, chatInterceptor);
        }

        this.interfaceManager.registerInterface(chatInterface);

        chatInterface.send();

        event.setCancelled(true);
        return;
    }
}
