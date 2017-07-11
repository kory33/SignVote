package com.github.kory33.signvote.vote

import com.github.kory33.signvote.constants.VotePointDataFileKeys
import com.github.kory33.signvote.utils.FileUtils
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import java.io.File
import java.io.IOException

/**
 * A class representing a sign of a vote point
 */
class VotePoint {
    var name: String
    var voteSign: Sign
        private set

    constructor(name: String, voteSign: Sign) {
        this.name = name
        this.voteSign = voteSign
    }

    @Throws(IllegalArgumentException::class, IOException::class)
    constructor(votePointFIle: File) {
        val jsonObject = FileUtils.readJSON(votePointFIle)

        this.name = jsonObject.get(VotePointDataFileKeys.NAME).asString

        val worldName = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_WORLD).asString
        val world = Bukkit.getWorld(worldName)
        val signX = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_X).asInt
        val signY = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Y).asInt
        val signZ = jsonObject.get(VotePointDataFileKeys.VOTE_SIGN_LOC_Z).asInt

        if (world == null) {
            throw IllegalArgumentException("Invalid file given! (Invalid world name $worldName)")
        }

        val state = world.getBlockAt(signX, signY, signZ).state as? Sign ?: throw IllegalStateException("Sign does not exist at the location in the saved data!")

        this.voteSign = state
    }

    /**
     * Get the json-serialized data of this object.
     * @return a json file that contains
     */
    fun toJson(): JsonObject {
        val jsonObject = JsonObject()

        jsonObject.addProperty(VotePointDataFileKeys.NAME, this.name)
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_WORLD, this.voteSign.world.name)
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_X, this.voteSign.x)
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_Y, this.voteSign.y)
        jsonObject.addProperty(VotePointDataFileKeys.VOTE_SIGN_LOC_Z, this.voteSign.z)

        return jsonObject
    }
}
