package com.github.kory33.signvote.ui.player.stats

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IBrowseablePageInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.StatsType
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.vote.VotePointStats
import java.util.function.Function
import org.bukkit.entity.Player

/**
 * Implementation of stats interface which sorts vote point statistics by mean score
 * @author Kory
 */
class MeanScoreStats : StatsInterface {
    internal constructor(player: Player, targetVoteSession: VoteSession, messageConfiguration: JSONConfiguration,
                         runnableInvoker: RunnableInvoker, interfaceManager: PlayerInteractiveInterfaceManager, pageIndex: Int) : super(player, targetVoteSession, messageConfiguration, runnableInvoker, interfaceManager, pageIndex)

    private constructor(oldInterface: MeanScoreStats, newIndex: Int) : super(oldInterface, newIndex)

    override fun yieldPage(pageIndex: Int): IBrowseablePageInterface {
        return MeanScoreStats(this, pageIndex)
    }

    override val statsType: StatsType
        get() = StatsType.MEAN

    override val statsComparator: Comparator<in VotePointStats> = Comparator
            .comparing(Function(VotePointStats::meanScore), Comparator.reverseOrder<Double>())

    override fun getStatsDisplayedValue(stats: VotePointStats): Number {
        return stats.meanScore
    }
}
