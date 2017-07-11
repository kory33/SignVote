package com.github.kory33.signvote.manager.vote

import com.github.kory33.signvote.utils.collection.CachingMap
import com.github.kory33.signvote.vote.Vote
import com.github.kory33.signvote.vote.VotePoint

import java.util.HashSet

/**
 * A map collection class that has [VotePoint] as keys and [<] as values.
 * This map automatically creates empty set upon call of [.get] method where value is `null`

 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */
/*package-private*/ internal class VotePointToVoteSetCacheMap
    : CachingMap<VotePoint, MutableSet<Vote>>({ HashSet() })