package com.github.kory33.signvote.exception

import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.vote.VotePoint

import java.util.UUID

/**
 * Represents an exception thrown when a cancel of non-existent vote has been requested.
 */
data class VotePointNotVotedException
constructor(val voterUUID: UUID, val votePoint: VotePoint, val session: VoteSession) : Exception()
