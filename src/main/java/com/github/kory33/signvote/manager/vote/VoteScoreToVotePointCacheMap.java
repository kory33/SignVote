package com.github.kory33.signvote.manager.vote;

import com.github.kory33.signvote.utils.collection.CachingMap;
import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.vote.VoteScore;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * A map collection class that has {@link VoteScore} as keys and {@link Set<VotePoint>} as values.
 * This map automatically creates empty value upon call of {@link #get(Object)} method where value is {@code null}
 *
 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */
/*package-private*/ class VoteScoreToVotePointCacheMap extends CachingMap<VoteScore, VotePointSet> {
    VoteScoreToVotePointCacheMap() {
        super(VotePointSet::new);
    }

    /**
     * Get a {@link JsonObject} representation of this object.
     * @return {@link JsonObject} representing this map.
     */
    JsonObject toJsonObject() {
        JsonObject resultObject = new JsonObject();
        this.toImmutableMap().forEach((voteScore, votePoints) ->
            resultObject.add(voteScore.toString(), votePoints.toNameJsonArray())
        );
        return resultObject;
    }
}