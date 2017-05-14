package com.github.kory33.signvote.ui.player.stats;

import java.util.Comparator;

import com.github.kory33.signvote.collection.RunnableInvoker;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.StatsType;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.model.VotePointStats;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

/**
 * Implementation of stats interface which sorts vote point statistics by total vote number.
 * @author Kory
 */
public final class TotalVoteStats extends StatsInterface {
    TotalVoteStats(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
                   RunnableInvoker runnableInvoker, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, targetVoteSession, messageConfiguration, runnableInvoker, interfaceManager, pageIndex);
    }

    private TotalVoteStats(TotalVoteStats oldInterface, int newIndex) {
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
