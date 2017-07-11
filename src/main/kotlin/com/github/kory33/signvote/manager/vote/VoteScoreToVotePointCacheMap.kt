package com.github.kory33.signvote.manager.vote

import com.github.kory33.signvote.utils.collection.CachingMap
import com.github.kory33.signvote.vote.VoteScore
import com.google.gson.JsonObject

/**
 * A map collection class that has [VoteScore] as keys and [<] as values.
 * This map automatically creates empty value upon call of [.get] method where value is `null`

 * @see com.github.kory33.signvote.utils.collection.CachingMap
 */
/*package-private*/ internal class VoteScoreToVotePointCacheMap
    : CachingMap<VoteScore, VotePointSet>({ VotePointSet() }) {

    /**
     * Get a [JsonObject] representation of this object.
     * @return [JsonObject] representing this map.
     */
    fun toJsonObject(): JsonObject {
        val resultObject = JsonObject()
        this.toMap().forEach { voteScore, votePoints -> resultObject.add(voteScore.toString(), votePoints.toNameJsonArray()) }
        return resultObject
    }
}