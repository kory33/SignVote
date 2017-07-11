package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.entity.Player

/**
 * Represents an exception thrown when a player attempts to vote with an invalid score
 */
data class InvalidScoreVotedException
constructor(val targetVotePoint: VotePoint, val voter: Player, val voteScore: VoteScore) : Exception()
