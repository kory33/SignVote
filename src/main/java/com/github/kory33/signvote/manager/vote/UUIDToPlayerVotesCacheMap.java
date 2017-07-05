package com.github.kory33.signvote.manager.vote;

import com.github.kory33.signvote.utils.collection.CachingMap;

import java.util.UUID;

/**
 * A map collection class that has {@link UUID} as keys and {@link VoteScoreToVotePointCacheMap} as values.
 * This map automatically creates empty value upon call of {@link #get(Object)} method where value is {@code null}
 *
 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */
/*package-private*/ class UUIDToPlayerVotesCacheMap extends CachingMap<UUID, VoteScoreToVotePointCacheMap> {
    UUIDToPlayerVotesCacheMap() {
        super(VoteScoreToVotePointCacheMap::new);
    }
}
