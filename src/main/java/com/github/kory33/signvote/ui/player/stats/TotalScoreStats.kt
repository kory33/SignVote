package com.github.kory33.signvote.ui.player.stats

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IBrowseablePageInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.StatsType
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.vote.VotePointStats
import org.bukkit.entity.Player

import java.util.Comparator
import java.util.function.Function

/**
 * Implementation of stats interface which sorts vote point statistics by total voted score.
 * @author Kory
 */
class TotalScoreStats : StatsInterface {
    internal constructor(player: Player, targetVoteSession: VoteSession, messageConfiguration: JSONConfiguration,
                         runnableInvoker: RunnableInvoker, interfaceManager: PlayerInteractiveInterfaceManager, pageIndex: Int) : super(player, targetVoteSession, messageConfiguration, runnableInvoker, interfaceManager, pageIndex)

    private constructor(oldInterface: StatsInterface, newPageIndex: Int) : super(oldInterface, newPageIndex)

    override fun yieldPage(pageIndex: Int): IBrowseablePageInterface {
        return TotalScoreStats(this, pageIndex)
    }

    override val statsType: StatsType
        get() = StatsType.SCORE

    override val statsComparator: Comparator<in VotePointStats>
        get() = Comparator.comparing(Function { it.totalScores }, Comparator.reverseOrder<Int>())

    override fun getStatsDisplayedValue(stats: VotePointStats): Number {
        return stats.totalScores
    }
}
