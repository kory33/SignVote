package com.github.kory33.signvote.vote

import java.util.UUID

/**
 * Abstract representation of a vote.
 * @author Kory
 */
data class Vote
constructor(val score: VoteScore, val voterUuid: UUID)
