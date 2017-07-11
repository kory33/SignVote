package com.github.kory33.signvote.exception

import com.github.kory33.signvote.session.VoteSession

/**
 * Represents an exception thrown when some interaction is attempted against a closed session.
 */
data class VoteSessionClosedException
constructor(val session: VoteSession) : Exception()
