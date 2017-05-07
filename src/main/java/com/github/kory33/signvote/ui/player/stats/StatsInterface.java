package com.github.kory33.signvote.ui.player.stats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.StatsType;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.model.VotePointStats;
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
     * Get the statistics type associated with the interface
     * @return
     */
    public abstract StatsType getStatsType();

    public abstract Number getStatsDisplayedValue(VotePointStats stats);

    /**
     * Get the comparator that determines the order of statistics entries
     * @return
     */
    public abstract Comparator<? super VotePointStats> getStatsComparator();

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

    @Override
    protected MessagePartsList getHeading() {
        MessagePartsList messagePartsList = new MessagePartsList();
        String statsType = this.messageConfig.getString(this.getStatsType().getTypeMessageNode());
        messagePartsList.addLine(getFormattedMessagePart(MessageConfigNodes.F_STATS_UI_HEADING,
                targetVoteSession.getName(), statsType));
        return messagePartsList;
    }

    @Override
    protected ArrayList<MessagePartsList> getEntryList() {
        ArrayList<MessagePartsList> entryList = new ArrayList<>();

        Stream<VotePointStats> sortedStatsStream = this.targetVoteSession.getAllVotePoints()
                .stream()
                .map(votePoint -> new VotePointStats(this.targetVoteSession, votePoint))
                .sorted(this.getStatsComparator());

        int index = 1;
        for (Iterator<VotePointStats> iterator = sortedStatsStream.iterator(); iterator.hasNext(); index++) {
            VotePointStats stats = iterator.next();
            MessagePartsList entry = new MessagePartsList();

            entry.add(this.getFormattedMessagePart(MessageConfigNodes.F_STATS_ENTRY_TEMPLATE, index,
                    stats.getVotePoint().getName(), this.getStatsDisplayedValue(stats)));

            entryList.add(entry);
        }

        return entryList;
    }
}
