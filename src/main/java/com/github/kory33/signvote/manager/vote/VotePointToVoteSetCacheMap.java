package com.github.kory33.signvote.manager.vote;

import com.github.kory33.signvote.utils.collection.CachingMap;
import com.github.kory33.signvote.vote.Vote;
import com.github.kory33.signvote.vote.VotePoint;

import java.util.HashSet;
import java.util.Set;

/**
 * A map collection class that has {@link VotePoint} as keys and {@link Set<Vote>} as values.
 * This map automatically creates empty set upon call of {@link #get(Object)} method where value is {@code null}
 *
 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */
/*package-private*/ class VotePointToVoteSetCacheMap extends CachingMap<VotePoint, Set<Vote>> {
    VotePointToVoteSetCacheMap() {
        super(HashSet::new);
    }
}