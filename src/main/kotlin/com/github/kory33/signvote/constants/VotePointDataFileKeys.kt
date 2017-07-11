package com.github.kory33.signvote.constants

/**
 * Keys in json file which stores information about vote points
 * @author Kory
 */
object VotePointDataFileKeys {
    val NAME = "name"

    private val VOTE_SIGN = "votesign"
    val VOTE_SIGN_WORLD = VOTE_SIGN + ".world"
    private val VOTE_SIGN_LOC = VOTE_SIGN + ".location"
    val VOTE_SIGN_LOC_X = VOTE_SIGN_LOC + ".X"
    val VOTE_SIGN_LOC_Y = VOTE_SIGN_LOC + ".Y"
    val VOTE_SIGN_LOC_Z = VOTE_SIGN_LOC + ".Z"
}
