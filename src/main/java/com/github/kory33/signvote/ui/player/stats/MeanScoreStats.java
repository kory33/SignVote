package com.github.kory33.signvote.ui.player.stats;

import java.util.Comparator;

import org.bukkit.entity.Player;

import com.github.kory33.chatgui.command.RunnableInvoker;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.StatsType;
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.vote.VotePointStats;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.chatgui.model.player.BrowseablePageInterface;

/**
 * Implementation of stats interface which sorts vote point statistics by mean score
 * @author Kory
 */
public final class MeanScoreStats extends StatsInterface {
    MeanScoreStats(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
                   RunnableInvoker runnableInvoker, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, targetVoteSession, messageConfiguration, runnableInvoker, interfaceManager, pageIndex);
    }

    private MeanScoreStats(MeanScoreStats oldInterface, int newIndex) {
        super(oldInterface, newIndex);
    }

    @Override
    protected BrowseablePageInterface yieldPage(int pageIndex) {
        return new MeanScoreStats(this, pageIndex);
    }

    @Override
    public StatsType getStatsType() {
        return StatsType.MEAN;
    }

    @Override
    public Comparator<? super VotePointStats> getStatsComparator() {
        return Comparator.comparing(VotePointStats::getMeanScore, Comparator.reverseOrder());
    }

    @Override
    public Number getStatsDisplayedValue(VotePointStats stats) {
        return stats.getMeanScore();
    }
}
