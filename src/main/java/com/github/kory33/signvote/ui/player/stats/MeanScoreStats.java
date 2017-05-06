package com.github.kory33.signvote.ui.player.stats;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

public final class MeanScoreStats extends StatsInterface {
    public MeanScoreStats(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, targetVoteSession, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
    }

    public MeanScoreStats(MeanScoreStats oldInterface, int newIndex) {
        super(oldInterface, newIndex);
    }

    @Override
    protected ArrayList<MessagePartsList> getEntryList() {
        ArrayList<MessagePartsList> entryList = new ArrayList<>();

        // TODO implementations

        return entryList;
    }

    @Override
    protected BrowseablePageInterface yieldPage(int pageIndex) {
        return new MeanScoreStats(this, pageIndex);
    }

    @Override
    protected MessagePartsList getHeading() {
        // TODO implementations
        return null;
    }
}
