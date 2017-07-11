package com.github.kory33.signvote.manager

import com.github.kory33.signvote.exception.data.InvalidLimitDataException
import com.github.kory33.signvote.vote.Limit
import com.github.kory33.signvote.vote.VoteLimit
import com.github.kory33.signvote.vote.VoteScore
import com.google.gson.JsonArray
import org.bukkit.entity.Player
import java.util.*

/**
 * A class that manages a collection of vote-limits
 */
class VoteLimitManager {
    private val limitSet: MutableSet<VoteLimit>

    init {
        this.limitSet = HashSet<VoteLimit>()
    }

    fun addVoteLimit(voteLimit: VoteLimit) {
        this.limitSet.add(voteLimit)
    }

    /**
     * Convert this object into a json array
     * @return converted json array
     */
    fun toJsonArray(): JsonArray {
        val resultArray = JsonArray()
        this.limitSet.forEach { limit -> resultArray.add(limit.toJsonObject()) }

        return resultArray
    }

    /**
     * Get a set of all the scores that may be cast by an OP player.
     * @return set containing [VoteScore] object
     */
    val votableScores: Set<VoteScore>
        get() = this.limitSet.map { it.score }.toSet()

    /**
     * Get the maximum limit of times the given player can vote with the given score.
     *
     *
     * Limit object returned by this method does not consider the votes which the player has already casted.
     *
     * @param score target score
     * *
     * @param player target player
     * *
     * @return [Limit] object representing number of votes the player may cast.
     */
    fun getLimit(score: VoteScore, player: Player): Limit {
        return limitSet
                .filter { voteLimit -> voteLimit.score == score && voteLimit.isApplicable(player) }
                .map { it.limit }
                .max() ?: Limit.zero
    }

    /**
     * Get a map of [VoteScore] to [Limit].
     *
     *
     * Returned map has scores which a given player can vote as keys,
     * and the limits of votes by the player with an associated score as values.
     *
     * @param player target player
     * *
     * @return Map of [VoteScore] to [Limit].
     */
    fun getLimitSet(player: Player): Map<VoteScore, Limit> {
        return this.votableScores.associate { Pair(it, this.getLimit(it, player)) }
    }

    companion object {

        /**
         * Construct a [VoteLimitManager] object from a json object.

         *
         *
         * Any object in the array which does not conform to the format of VoteLimit object will be ignored.
         *
         * @param voteLimitsJsonArray json array from which the data will be read
         * *
         * @return constructed manager object
         */
        fun fromJsonArray(voteLimitsJsonArray: JsonArray): VoteLimitManager {
            val voteLimitManager = VoteLimitManager()

            voteLimitsJsonArray.forEach { element ->
                try {
                    voteLimitManager.addVoteLimit(VoteLimit.fromJsonObject(element.asJsonObject))
                } catch (e: InvalidLimitDataException) {
                    print("Ignoring json element : " + element.toString())
                }
            }

            return voteLimitManager
        }
    }
}
