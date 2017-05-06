package com.github.kory33.signvote.ui.player;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;

public final class StatsTypeSelectionInterface extends PlayerClickableChatInterface {
    private final VoteSession targetSession;
    private final PlayerInteractiveInterfaceManager interfaceManager;

    public StatsTypeSelectionInterface(Player player, VoteSession session, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager) {
        super(player, messageConfiguration, runnableHashTable);
        this.targetSession = session;
        this.interfaceManager = interfaceManager;
    }

    @Override
    protected MessagePartsList getBodyMessages() {
        // TODO implementations
        return null;
    }
}
