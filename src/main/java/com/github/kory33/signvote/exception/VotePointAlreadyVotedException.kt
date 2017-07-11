package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint

import java.util.UUID

/**
 * Represents an exception thrown when a player attempts to vote to a vote point
 * which has already been voted by the same player.
 */
data class VotePointAlreadyVotedException
constructor(val voterUUID: UUID, val votePoint: VotePoint) : Exception()
