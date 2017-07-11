package com.github.kory33.signvote.ui.player.stats

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IBrowseablePageInterface
import com.github.kory33.chatgui.tellraw.MessagePartsList
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.StatsType
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.ui.player.defaults.DefaultBrowseableInterface
import com.github.kory33.signvote.vote.VotePointStats
import com.github.ucchyocean.messaging.tellraw.MessageParts
import org.bukkit.entity.Player

import java.util.ArrayList
import java.util.Comparator

/**
 * Abstraction of statistics interface which can be browsed by a player.
 * @author Kory
 */
abstract class StatsInterface : IBrowseablePageInterface, DefaultBrowseableInterface {
    private val targetVoteSession: VoteSession

    internal constructor(player: Player, targetVoteSession: VoteSession, messageConfig: JSONConfiguration,
                         runnableInvoker: RunnableInvoker, interfaceManager: PlayerInteractiveInterfaceManager,
                         pageIndex: Int) : super(player, runnableInvoker, messageConfig, interfaceManager, pageIndex) {
        this.targetVoteSession = targetVoteSession
    }

    protected constructor(oldInterface: StatsInterface, newPageIndex: Int) : super(oldInterface, newPageIndex) {

        this.targetVoteSession = oldInterface.targetVoteSession
    }

    /**
     * Get the statistics type associated with the interface
     * @return statistics type
     */
    abstract val statsType: StatsType

    abstract fun getStatsDisplayedValue(stats: VotePointStats): Number

    /**
     * Get the comparator that determines the order of statistics entries
     * @return comparator that compares statistics data and determines the order of entries
     */
    abstract val statsComparator: Comparator<in VotePointStats>

    /**
     * Get a message formatted with the given array of Object arguments(optional)
     * @param configurationNode configuration node from which the message should be fetched
     * *
     * @param objects objects used in formatting the fetched string
     * *
     * @return formatted message component
     */
    private fun getFormattedMessagePart(configurationNode: String, vararg objects: Any): MessageParts {
        return MessageParts(this.messageConfig.getFormatted(configurationNode, *objects))
    }

    override val entryList: ArrayList<MessagePartsList>
        get() {
            val entryList = ArrayList<MessagePartsList>()

            val sortedStatsStream = this.targetVoteSession.allVotePoints
                    .stream()
                    .map { votePoint -> VotePointStats(this.targetVoteSession, votePoint) }
                    .sorted(this.statsComparator)

            var index = 1
            val iterator = sortedStatsStream.iterator()
            while (iterator.hasNext()) {
                val stats = iterator.next()
                val entry = MessagePartsList()

                entry.add(this.getFormattedMessagePart(MessageConfigNodes.F_STATS_ENTRY_TEMPLATE, index,
                        stats.votePoint.name, this.getStatsDisplayedValue(stats)))

                entryList.add(entry)
                index++
            }

            return entryList
        }

    override val heading: MessagePartsList
        get() {
            val messagePartsList = MessagePartsList()
            val statsType = this.messageConfig.getString(this.statsType.typeMessageNode)
            messagePartsList.addLine(getFormattedMessagePart(MessageConfigNodes.F_STATS_UI_HEADING,
                    targetVoteSession.name, statsType))
            return messagePartsList
        }
    companion object {

        /**
         * Create a new stats interface with given information
         * @param sender A CommandSender to which the interface should be sent
         * *
         * @param session The session against which the statistics should be taken
         * *
         * @param statsType The type of statistics
         * *
         * @param pageIndex Index of the statistics ranking page.
         * *
         * @return statistics interface instance
         */
        fun createNewInterface(sender: Player, session: VoteSession, statsType: String, pageIndex: Int,
                               messageConfiguration: JSONConfiguration, runnableInvoker: RunnableInvoker, interfaceManager: PlayerInteractiveInterfaceManager): StatsInterface? {
            try {
                val targetStatsType = StatsType.fromString(statsType)
                when (targetStatsType) {
                    StatsType.VOTES -> return TotalVoteStats(sender, session, messageConfiguration, runnableInvoker, interfaceManager, pageIndex)
                    StatsType.SCORE -> return TotalScoreStats(sender, session, messageConfiguration, runnableInvoker, interfaceManager, pageIndex)
                    StatsType.MEAN -> return MeanScoreStats(sender, session, messageConfiguration, runnableInvoker, interfaceManager, pageIndex)
                }
            } catch (ignored: IllegalArgumentException) {
            }

            return null
        }
    }
}
