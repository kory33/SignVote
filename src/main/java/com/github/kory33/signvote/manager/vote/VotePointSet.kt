package com.github.kory33.signvote.manager.vote

import com.github.kory33.signvote.vote.VotePoint
import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import java.util.*

/**
 * This class extends [HashSet] with a type parameter of [VotePoint].

 * No method is overrode, but a method [JsonArray] is added.
 */
/*package-private*/ internal class VotePointSet : HashSet<VotePoint>() {
    /**
     * Convert this object to a json-array that consists of strings representing name of each vote-point.
     * @return converted object
     */
    fun toNameJsonArray(): JsonArray {
        val array = JsonArray()
        this.map({ JsonPrimitive(it.name) }).forEach({ array.add(it) })
        return array
    }
}
