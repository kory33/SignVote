package com.github.kory33.signvote.vote

import com.github.kory33.signvote.exception.data.InvalidLimitDataException
import com.google.gson.JsonObject
import org.bukkit.entity.Player

/**
 * A class that represents a limit of vote counts
 */
data class VoteLimit
constructor(val score: VoteScore, val limit: Limit, private val permission: String?) {

    constructor(score: VoteScore, limit: Limit) : this(score, limit, null)

    /**
     * Returns if the limit applies to the given player
     * @param player player to be inspected
     * *
     * @return true if the player has permissions associated to this vote-limit
     */
    fun isApplicable(player: Player): Boolean {
        return this.permission == null || player.hasPermission(this.permission)
    }

    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject()

        jsonObject.addProperty(JSON_SCORE_KEY, this.score.toInt())
        jsonObject.addProperty(JSON_LIMIT_KEY, this.limit.toString())
        jsonObject.addProperty(JSON_PERMS_KEY, this.permission)

        return jsonObject
    }

    companion object {
        /** Json keys  */
        private val JSON_SCORE_KEY = "score"
        private val JSON_LIMIT_KEY = "limit"
        private val JSON_PERMS_KEY = "permission"

        /**
         * Construct a VoteLimit object from a json object
         * @param jsonObject object to be converted to a VoteLimit
         * *
         * @return converted object
         * *
         * @throws InvalidLimitDataException when the given jsonObject is invalid
         */
        @Throws(InvalidLimitDataException::class)
        fun fromJsonObject(jsonObject: JsonObject): VoteLimit {
            val scoreElement = jsonObject.get(JSON_SCORE_KEY)
            val limitElement = jsonObject.get(JSON_LIMIT_KEY)
            val permissionElement = jsonObject.get(JSON_PERMS_KEY)

            val score = VoteScore(scoreElement.asInt)
            val limit = Limit.fromString(limitElement.asString)

            if (permissionElement.isJsonNull) {
                return VoteLimit(score, limit)
            }

            val permissionString = permissionElement.asString
            return VoteLimit(score, limit, permissionString)
        }
    }
}
