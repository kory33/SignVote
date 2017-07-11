package com.github.kory33.signvote.constants

/**
 * Collection of permission nodes used by SignVote plugin
 * @author Kory
 */
object PermissionNodes {
    private val BASE_NODE = "signvote"

    val VOTE = BASE_NODE + ".vote"
    val VOTE_MORE = BASE_NODE + ".votemore"

    val UNVOTE = BASE_NODE + ".unvote"
    val CREATE_SIGN = BASE_NODE + ".createsign"
    val CREATE_SESSION = BASE_NODE + ".createsession"
    val MODIFY_SESSION = BASE_NODE + ".modifysession"

    val OPEN_SESSION = BASE_NODE + ".opensession"
    val CLOSE_SESSION = BASE_NODE + ".closesession"

    val DELETE_VOTEPOINT = BASE_NODE + ".deletevotepoint"

    val DELETE_SESSION = BASE_NODE + ".deletesession"

    val RELOAD = BASE_NODE + ".reload"

    val SAVE = BASE_NODE + ".save"

    val LIST_SESSION = BASE_NODE + ".listsession"

    val VIEW_STATS = BASE_NODE + ".viewstats"
}
