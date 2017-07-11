package com.github.kory33.signvote.session

import com.github.kory33.chatgui.util.collection.BijectiveHashMap
import com.github.kory33.signvote.constants.*
import com.github.kory33.signvote.exception.*
import com.github.kory33.signvote.manager.VoteLimitManager
import com.github.kory33.signvote.manager.vote.VoteManager
import com.github.kory33.signvote.utils.FileUtils
import com.github.kory33.signvote.vote.Limit
import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

class VoteSession {
    private val signMap: BijectiveHashMap<Sign, VotePoint>
    private val votePointNameMap: BijectiveHashMap<String, VotePoint>

    val voteLimitManager: VoteLimitManager
    val name: String
    val voteManager: VoteManager

    var isOpen: Boolean = false

    /**
     * Constructs the vote session from the given session folder
     * @param sessionSaveLocation directory from which session data is read
     * *
     * @throws IllegalArgumentException when the session folder is invalid
     */
    @Throws(IllegalArgumentException::class, IOException::class)
    constructor(sessionSaveLocation: File) {
        this.signMap = BijectiveHashMap<Sign, VotePoint>()
        this.votePointNameMap = BijectiveHashMap<String, VotePoint>()

        // read information of this vote session
        val sessionDataFile = File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME)

        val sessionConfigJson = FileUtils.readJSON(sessionDataFile)
        val voteLimitsJsonArray = sessionConfigJson
                .get(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS)
                .asJsonArray

        this.voteLimitManager = VoteLimitManager.fromJsonArray(voteLimitsJsonArray)
        this.name = sessionConfigJson.get(VoteSessionDataFileKeys.NAME).asString

        this.isOpen = sessionConfigJson.get(VoteSessionDataFileKeys.IS_OPEN).asBoolean

        // load all the saved votepoints
        val votePointDirectory = File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR)
        FileUtils.getFileListStream(votePointDirectory).forEach( { this.addVotePoint(it) })

        // initialize vote manager
        this.voteManager = VoteManager(File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR), this)
    }

    /**
     * Constructs the vote session from its parameters.
     * @param sessionName name of the session
     */
    constructor(sessionName: String) {
        this.name = sessionName

        this.voteLimitManager = VoteLimitManager()
        this.voteManager = VoteManager(this)

        this.signMap = BijectiveHashMap<Sign, VotePoint>()
        this.votePointNameMap = BijectiveHashMap<String, VotePoint>()

        this.isOpen = true
    }

    /**
     * Load a votepoint from the existing votepoint data file.
     * @param votePointFIle file which contains information about vote point
     */
    private fun addVotePoint(votePointFIle: File) {
        try {
            val votePoint = VotePoint(votePointFIle)
            this.addVotePoint(votePoint)
        } catch (e: Exception) {
            Bukkit.getLogger().log(Level.SEVERE, "", e)
        }

    }

    /**
     * add a vote point to the session
     * @param votePoint vote point
     */
    fun addVotePoint(votePoint: VotePoint) {
        this.signMap.put(votePoint.voteSign, votePoint)
        this.votePointNameMap.put(votePoint.name, votePoint)
    }

    /**
     * Get Json object containing information directly related to this object
     * @return json object containing information about session
     */
    private fun toJson(): JsonObject {
        val jsonObject = JsonObject()

        jsonObject.addProperty(VoteSessionDataFileKeys.NAME, this.name)
        jsonObject.add(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS, this.voteLimitManager.toJsonArray())
        jsonObject.addProperty(VoteSessionDataFileKeys.IS_OPEN, this.isOpen)

        return jsonObject
    }

    /**
     * purge non-registered votepoint files under a given directory.
     * @param votePointDirectory directory which contains vote point directory
     */
    private fun purgeInvalidVpFiles(votePointDirectory: File) {
        val nonExistentVpFiles = FileUtils.getFileListStream(votePointDirectory).filter { file ->
            val matcher = Patterns.JSON_FILE_NAME.matcher(file.name)
            !matcher.find() || !this.votePointNameMap.containsKey(matcher.group(1))
        }

        CompletableFuture.runAsync {
            nonExistentVpFiles.forEach { file ->
                println("Deleting " + file.name)

                file.delete()
            }
        }
    }

    /**
     * Save the session data to the given directory.
     * @param sessionSaveLocation location to which session data is saved
     * *
     * @throws IOException when any error occurs whilst saving files
     */
    @Throws(IOException::class)
    fun saveTo(sessionSaveLocation: File) {
        if (!sessionSaveLocation.exists() && !sessionSaveLocation.mkdirs()) {
            throw IOException("Failed to create vote session save directory!")
        } else if (!sessionSaveLocation.isDirectory) {
            throw IOException("Vote session was about to be saved into a file! (" + sessionSaveLocation.absolutePath + ")")
        }

        // initialize vote point dir
        val votePointDirectory = File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR)
        if (!votePointDirectory.exists() && !votePointDirectory.mkdirs()) {
            throw IOException("Failed to create vote point save directory!")
        }
        this.purgeInvalidVpFiles(votePointDirectory)

        // save vote points
        signMap.getInverse().keys.stream().parallel()
                .forEach { votePoint ->
                    val saveTarget = File(votePointDirectory, votePoint.name + Formats.JSON_EXT)
                    FileUtils.writeJSON(saveTarget, votePoint.toJson())
                }

        // save vote data
        val voteDataDirectory = File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR)
        if (!voteDataDirectory.exists() && !voteDataDirectory.mkdirs()) {
            throw IOException("Failed to create vote data directory!")
        }
        this.voteManager.playersVoteData.forEach { playerUUID, voteData ->
            val playerVoteDataFile = File(voteDataDirectory, playerUUID.toString() + Formats.JSON_EXT)

            FileUtils.writeJSON(playerVoteDataFile, voteData)
        }

        // write session data
        val sessionDataFile = File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME)
        val jsonData = this.toJson()
        FileUtils.writeJSON(sessionDataFile, jsonData)
    }

    /**
     * Get a VotePoint associated with a given Sign.
     * @param sign vote point sign
     * *
     * @return vote point instance associated with sign, null if sign is not a vote point
     */
    fun getVotePoint(sign: Sign): VotePoint? {
        return this.signMap[sign]
    }

    fun getVotePoint(pointName: String): VotePoint? {
        return this.votePointNameMap[pointName]
    }

    /**
     * Get a score -> count map of available votes for a given player
     * @param player player instance
     * *
     * @return map of score -> reserved limit
     * * limit is an optional with limit value, empty if infinite number of votes can be casted
     */
    fun getAvailableVoteCounts(player: Player): Map<VoteScore, Limit> {
        val availableCounts = this.getReservedVoteCounts(player).toMutableMap()
        val votedScoreCounts = this.voteManager.getVotedPointsCount(player.uniqueId)

        votedScoreCounts.forEach { score, votedNum ->
            if (!availableCounts.containsKey(score)) {
                return@forEach
            }

            val reservedVotes = availableCounts.remove(score)!!
            val remainingVotes = reservedVotes - votedNum

            // iff remaining != 0
            if (!remainingVotes.isZero) {
                availableCounts.put(score, remainingVotes)
            }
        }

        return availableCounts
    }

    /**
     * Get a score -> count map of reserved votes for a given player
     * @param player player instance
     * *
     * @return map of score -> available limit
     * * limit is an optional with limit value, empty if infinite number of votes can be casted
     */
    fun getReservedVoteCounts(player: Player): Map<VoteScore, Limit> {
        return this.voteLimitManager.getLimitSet(player)
    }

    /**
     * Make a vote to the specified votepoint with a given score.
     * Score has to be checked for it's validity,
     * but may not be checked for player vote limits as an exception is thrown
     * @param player player who is attempting to vote
     * *
     * @param votePoint vote point to which the player is attempting to vote
     * *
     * @param voteScore score of vote which the player is attempting to cast
     * *
     * *
     * @throws ScoreCountLimitReachedException when the player can no longer vote with the given score due to the limit
     * *
     * @throws VotePointAlreadyVotedException when the player has already voted to the votepoint
     * *
     * @throws VoteSessionClosedException when this vote session is closed
     */
    @Throws(ScoreCountLimitReachedException::class, VotePointAlreadyVotedException::class, InvalidScoreVotedException::class, VoteSessionClosedException::class)
    fun vote(player: Player, votePoint: VotePoint, voteScore: VoteScore) {
        if (this.voteLimitManager.getLimit(voteScore, player).isZero) {
            throw InvalidScoreVotedException(votePoint, player, voteScore)
        }

        if (!this.getReservedVoteCounts(player).containsKey(voteScore)) {
            throw ScoreCountLimitReachedException(player, votePoint, voteScore)
        }

        if (!this.getAvailableVoteCounts(player).containsKey(voteScore)) {
            throw ScoreCountLimitReachedException(player, votePoint, voteScore)
        }

        if (!this.isOpen) {
            throw VoteSessionClosedException(this)
        }

        this.voteManager.addVotePointData(player.uniqueId, voteScore, votePoint)
    }

    /**
     * Cancel a vote to the specified votepoint made by a given player.
     * @param player player who is trying to cancel a vote
     * *
     * @param votePoint vote point from which the vote should be removed
     * *
     * @throws VotePointNotVotedException When the player hasn't voted the votepoint.
     */
    @Throws(VotePointNotVotedException::class)
    fun unvote(player: Player, votePoint: VotePoint) {
        this.voteManager.removeVote(player.uniqueId, votePoint)
    }

    /**
     * Delete the specified votepoint
     * @param votePoint target vote point
     */
    fun deleteVotepoint(votePoint: VotePoint) {
        this.voteManager.removeAllVotes(votePoint)

        this.votePointNameMap.removeValue(votePoint)
        val sign = this.signMap.removeValue(votePoint) ?: return

        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT)
        sign.setLine(1, SignTexts.DELETED)
        sign.setLine(2, "")
        sign.update()
    }

    /**
     * Get all the votepoints registered to this vote session
     */
    val allVotePoints: Set<VotePoint>
        get() = this.votePointNameMap.getInverse().keys
}
