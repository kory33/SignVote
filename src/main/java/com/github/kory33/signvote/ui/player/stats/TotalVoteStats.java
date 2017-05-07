package com.github.kory33.signvote.ui.player.stats;

import java.util.Comparator;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.StatsType;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.model.VotePointStats;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

public final class TotalVoteStats extends StatsInterface {
    public TotalVoteStats(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, targetVoteSession, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
    }

    public TotalVoteStats(TotalVoteStats oldInterface, int newIndex) {
        super(oldInterface, newIndex);
    }


    @Override
    protected BrowseablePageInterface yieldPage(int pageIndex) {
        return new TotalVoteStats(this, pageIndex);
    }

    @Override
    public StatsType getStatsType() {
        return StatsType.VOTES;
    }

    @Override
    public Comparator<? super VotePointStats> getStatsComparator() {
        return Comparator.comparing(VotePointStats::getTotalVotes, Comparator.reverseOrder());
    }

    @Override
    public Number getStatsDisplayedValue(VotePointStats stats) {
        return stats.getTotalVotes();
    }
}
