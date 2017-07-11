package com.github.kory33.signvote.manager.vote

import com.github.kory33.signvote.utils.collection.CachingMap

import java.util.UUID

/**
 * A map collection class that has [UUID] as keys and [VoteScoreToVotePointCacheMap] as values.
 * This map automatically creates empty value upon call of [.get] method where value is `null`

 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */

internal class UUIDToPlayerVotesCacheMap :
        CachingMap<UUID, VoteScoreToVotePointCacheMap>({ VoteScoreToVotePointCacheMap() })
