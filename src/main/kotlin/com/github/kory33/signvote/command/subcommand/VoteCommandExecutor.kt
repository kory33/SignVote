package com.github.kory33.signvote.command.subcommand

import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import com.github.kory33.signvote.constants.PermissionNodes
import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.exception.InvalidScoreVotedException
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException
import com.github.kory33.signvote.exception.VoteSessionClosedException
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * Executor class of "vote" sub-command
 * @author Kory
 */
class VoteCommandExecutor(plugin: SignVote) : SubCommandExecutor {
    private val messageConfiguration: JSONConfiguration = plugin.messagesConfiguration!!
    private val voteSessionManager: VoteSessionManager = plugin.voteSessionManager!!

    override val helpString: String
        get() = this.messageConfiguration.getString(MessageConfigNodes.VOTE_COMMAND_HELP)

    override fun onCommand(sender: CommandSender, command: Command, args: ArrayList<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.COMMAND_ONLY_FOR_PLAYERS))
            return true
        }

        if (!sender.hasPermission(PermissionNodes.VOTE)) {
            sender.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.MISSING_PERMS))
            return true
        }

        if (args.size != 3) {
            return false
        }

        val player = sender
        val sessionName = args.removeAt(0)
        val votePointName = args.removeAt(0)
        val voteScoreString = args.removeAt(0)

        val session = this.voteSessionManager.getVoteSession(sessionName)
        if (session == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST))
            return true
        }

        val votePoint = session.getVotePoint(votePointName)
        if (votePoint == null) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_DOES_NOT_EXIST))
            return true
        }

        val voteScore: VoteScore
        try {
            voteScore = VoteScore(Integer.parseInt(voteScoreString))
        } catch (exception: Exception) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE))
            return true
        }

        // if the voter does not have vote score reserved
        val reservedVotes = session.getReservedVoteCounts(player)[voteScore]
        if (reservedVotes == null || reservedVotes.isZero) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE))
            return true
        }

        try {
            session.vote(player, votePoint, voteScore)
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTED))
        } catch (exception: ScoreCountLimitReachedException) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.REACHED_VOTE_SCORE_LIMIT))
        } catch (exception: VotePointAlreadyVotedException) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTEPOINT_ALREADY_VOTED))
        } catch (exception: InvalidScoreVotedException) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.INVALID_VOTE_SCORE))
        } catch (exception: VoteSessionClosedException) {
            player.sendMessage(this.messageConfiguration.getString(MessageConfigNodes.VOTE_SESSION_CLOSED))
        }

        return true
    }

}
