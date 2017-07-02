package com.github.kory33.signvote.listeners;

import com.github.kory33.chatgui.command.RunnableInvoker;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.UnvoteInterface;
import com.github.kory33.signvote.ui.player.VoteInterface;
import com.github.kory33.chatgui.model.player.PlayerClickableChatInterface;

/**
 * A Listener implementation which listens to player's attempts to vote to a vote point
 */
public class PlayerVoteListener implements Listener {
    private final VoteSessionManager voteSessionManager;
    private final JSONConfiguration messageConfig;
    private final RunnableInvoker runnableInvoker;
    private final PlayerInteractiveInterfaceManager interfaceManager;
    private final SignVote plugin;

    public PlayerVoteListener(SignVote plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.voteSessionManager = plugin.getVoteSessionManager();
        this.messageConfig = plugin.getMessagesConfiguration();
        this.runnableInvoker = plugin.getRunnableInvoker();
        this.interfaceManager = plugin.getInterfaceManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractWithVotePoint(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (!this.plugin.getAPI().isSignVoteSign(clickedBlock)) {
            return;
        }

        Sign sign = (Sign) clickedBlock.getState();

        VoteSession session = this.voteSessionManager.getVoteSession(sign);
        VotePoint votePoint = session.getVotePoint(sign);

        Player clickPlayer = event.getPlayer();
        PlayerClickableChatInterface chatInterface;
        if (session.getVoteManager().hasVoted(clickPlayer.getUniqueId(), votePoint)) {
            chatInterface = new UnvoteInterface(clickPlayer, session, votePoint, messageConfig, runnableInvoker);
        } else {
            chatInterface = new VoteInterface(clickPlayer, session, votePoint, messageConfig, runnableInvoker);
        }

        this.interfaceManager.registerInterface(chatInterface);

        chatInterface.send();

        event.setCancelled(true);
    }
}
