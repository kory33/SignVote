package com.github.kory33.signvote.manager.vote

import com.github.kory33.signvote.exception.VotePointAlreadyVotedException
import com.github.kory33.signvote.exception.VotePointNotVotedException
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.utils.FileUtils
import com.github.kory33.signvote.utils.transformValues
import com.github.kory33.signvote.vote.Vote
import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import com.google.gson.JsonObject
import java.io.File
import java.io.IOException
import java.util.*

/**
 * A class which handles all the vote data
 */
class VoteManager {
    private val cacheFromUUID: UUIDToPlayerVotesCacheMap
    private val cacheFromVotePoint: VotePointToVoteSetCacheMap
    private val parentSession: VoteSession

    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory directory in which vote data is stored player-wise
     * *
     * @param parentSession session which is responsible for votes that are about to be read
     * *
     * @throws IllegalArgumentException when null value or a non-directory file is given as a parameter
     */
    @Throws(IOException::class)
    constructor(voteDataDirectory: File?, parentSession: VoteSession) {
        this.parentSession = parentSession
        this.cacheFromUUID = UUIDToPlayerVotesCacheMap()
        this.cacheFromVotePoint = VotePointToVoteSetCacheMap()

        if (voteDataDirectory == null) {
            throw IllegalArgumentException("Directory cannot be null!")
        }

        if (!voteDataDirectory.isDirectory) {
            throw IOException("Directory has to be specified for save location!")
        }

        FileUtils.getFileListStream(voteDataDirectory).forEach { playerVoteDataFile ->
            var jsonObject: JsonObject? = null
            try {
                jsonObject = FileUtils.readJSON(playerVoteDataFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val uuid = UUID.fromString(FileUtils.getFileBaseName(playerVoteDataFile))
            this.loadPlayerVoteData(uuid, jsonObject!!)
        }
    }

    private fun loadPlayerVoteData(playerUUID: UUID, jsonObject: JsonObject) {
        jsonObject.entrySet().forEach { entry ->
            val score = VoteScore(Integer.parseInt(entry.key))

            entry.value.asJsonArray.forEach votePointLoad@{ elem ->
                val votePoint = this.parentSession.getVotePoint(elem.asString) ?: return@votePointLoad

                try {
                    this.addVotePointData(playerUUID, score, votePoint)
                } catch (ignored: VotePointAlreadyVotedException) {
                }
            }
        }
    }

    /**
     * Get the players' vote data, as a map of Player to JsonObject
     * @return mapping of (player's)UUID -> json object containing votes of the player
     */
    val playersVoteData: Map<UUID, JsonObject>
        get() = this.cacheFromUUID.toMap().transformValues { it.toJsonObject() }

    /**
     * Construct an empty VoteManager object.
     */
    constructor(parentSession: VoteSession) {
        this.cacheFromUUID = UUIDToPlayerVotesCacheMap()
        this.cacheFromVotePoint = VotePointToVoteSetCacheMap()
        this.parentSession = parentSession
    }

    /**
     * Get the mapping of voted score to a list of voted points
     */
    private fun getVotedPointsMap(uuid: UUID): VoteScoreToVotePointCacheMap {
        return this.cacheFromUUID[uuid]
    }

    /**
     * Get the mapping of [vote score] to the [number of times the score has been voted by the player]
     * @param uuid UUID of the player
     * *
     * @return A map containing vote scores as keys and vote counts(with the score of corresponding key) as values
     */
    fun getVotedPointsCount(uuid: UUID): Map<VoteScore, Int> {
        return this.getVotedPointsMap(uuid).toMap().transformValues { it.size }
    }

    /**
     * Add a vote data related to the score and the votepoint to which the player has voted
     * @param voterUUID UUID of a player who has voted
     * *
     * @param voteScore score which the player has voted
     * *
     * @param votePoint vote point to which the player has voted
     * *
     * @throws IllegalArgumentException when there is a duplicate in the vote
     */
    @Throws(VotePointAlreadyVotedException::class)
    fun addVotePointData(voterUUID: UUID, voteScore: VoteScore, votePoint: VotePoint) {
        val cacheByScores = this.cacheFromUUID[voterUUID]

        if (this.hasVoted(voterUUID, votePoint)) {
            throw VotePointAlreadyVotedException(voterUUID, votePoint)
        }

        cacheByScores[voteScore].add(votePoint)
        this.cacheFromVotePoint[votePoint].add(Vote(voteScore, voterUUID))
    }

    /**
     * Remove a vote casted by the given player to the given vote point.
     * @param playerUUID UUID of a player who tries to cancel the vote
     * *
     * @param votePoint vote point whose vote by the player is being cancelled
     * *
     * @throws VotePointNotVotedException when the player has not voted to the target vote point
     */
    @Throws(VotePointNotVotedException::class)
    fun removeVote(playerUUID: UUID, votePoint: VotePoint) {
        val playerVotes = this.getVotedPointsMap(playerUUID)

        val targetVotePointSet = playerVotes.toMap()
                .filter { voteScoreSetEntry -> voteScoreSetEntry.value.contains(votePoint) }
                .map({ it.value })
                .firstOrNull()

        val votePointToVoteSetCacheMap = this.cacheFromVotePoint
        val targetVoteCache = votePointToVoteSetCacheMap[votePoint]
                .filter { vote -> vote.voterUuid == playerUUID }
                .firstOrNull()

        if (targetVotePointSet == null || targetVoteCache == null) {
            throw VotePointNotVotedException(playerUUID, votePoint, this.parentSession)
        }

        targetVotePointSet.remove(votePoint)
        votePointToVoteSetCacheMap[votePoint].remove(targetVoteCache)
    }

    /**
     * Remove all the votes associated with the given votepoint.
     * @param votePoint vote point from which votes will be removed
     */
    fun removeAllVotes(votePoint: VotePoint) {
        val votes = this.cacheFromVotePoint[votePoint]

        // purge votepoint names present in cacheFromUUID
        votes.forEach { (score, voterUuid) ->
            try {
                this.cacheFromUUID[voterUuid][score].remove(votePoint)
            } catch (exception: NullPointerException) {
                // NPE should be thrown If and Only If
                // cacheFromUUID and cacheFromVotePoint are not synchronized correctly
                exception.printStackTrace()
            }
        }

        // clear cacheFromVotePoint
        this.cacheFromVotePoint.remove(votePoint)
    }

    /**
     * Get a set of votes casted to the given vote point.
     * @param votePoint target vote point
     * *
     * @return set containing all the votes casted to the vote point.
     */
    fun getVotes(votePoint: VotePoint): Set<Vote> {
        return this.cacheFromVotePoint[votePoint]
    }

    /**
     * Get the score a given player has voted to a given name of votepoint.
     * The returned optional object contains no value if the player has not voted.
     * @param playerUUID UUID of the player
     *
     * @param votePoint vote point from which score data is fetched
     *
     * @return an Optional object containing score vote by the player
     */
    fun getVotedScore(playerUUID: UUID, votePoint: VotePoint): VoteScore? {
        return this.getVotedPointsMap(playerUUID).toMap()
                .filter { entry -> entry.value.contains(votePoint) }
                .map({ it.key })
                .firstOrNull()
    }

    /**
     * Check if the given player has voted to the specified votepoint.
     * @param playerUUID UUID of the player
     * *
     * @param votePoint target vote point
     * *
     * @return boolean value true iff player has voted to the given vote point.
     */
    fun hasVoted(playerUUID: UUID, votePoint: VotePoint): Boolean {
        return this.getVotedScore(playerUUID, votePoint) != null
    }
}
