package com.github.kory33.signvote.ui.player.stats;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.StatsType;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

public abstract class StatsInterface extends BrowseablePageInterface {
    protected final VoteSession targetVoteSession;
    public StatsInterface(Player player, VoteSession targetVoteSession, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
        this.targetVoteSession = targetVoteSession;
    }

    public StatsInterface(StatsInterface oldInterface, int newPageIndex) {
        super(oldInterface, newPageIndex);
        this.targetVoteSession = oldInterface.targetVoteSession;
    }

    /**
     * Create a new stats interface with given information
     * @param sender A CommandSender to which the interface should be sent
     * @param session The session against which the statistics should be taken
     * @param statsType The type of statistics
     * @param pageIndex Index of the statistics ranking page.
     * @return
     */
    public static StatsInterface createNewInterface(Player sender, VoteSession session, String statsType, int pageIndex,
            JSONConfiguration messageConfiguration, RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager) {
        try {
            StatsType targetStatsType = StatsType.fromString(statsType);
            switch (targetStatsType) {
            case VOTES:
                return new TotalVoteStats(sender, session, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
            case SCORE:
                return new TotalScoreStats(sender, session, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
            case MEAN:
                return new MeanScoreStats(sender, session, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
            }
        } catch (IllegalArgumentException exception) {}

        return null;
    }
}
