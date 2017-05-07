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

public final class TotalScoreStats extends StatsInterface {
    public TotalScoreStats(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, targetVoteSession, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
    }

    public TotalScoreStats(StatsInterface oldInterface, int newPageIndex) {
        super(oldInterface, newPageIndex);
    }

    @Override
    protected BrowseablePageInterface yieldPage(int pageIndex) {
        return new TotalScoreStats(this, pageIndex);
    }

    @Override
    public StatsType getStatsType() {
        return StatsType.SCORE;
    }

    @Override
    public Comparator<? super VotePointStats> getStatsComparator() {
        return Comparator.comparing(VotePointStats::getTotalScores, Comparator.reverseOrder());
    }

    @Override
    public Number getStatsDisplayedValue(VotePointStats stats) {
        return stats.getTotalScores();
    }
}
