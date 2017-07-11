package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.entity.Player

/**
 * Represents an exception thrown when a player attempts to vote with a score
 * whose vote limit has already been reached.
 */
data class ScoreCountLimitReachedException
constructor(val player: Player, val voteTarget: VotePoint, val voteScore: VoteScore) : Exception()