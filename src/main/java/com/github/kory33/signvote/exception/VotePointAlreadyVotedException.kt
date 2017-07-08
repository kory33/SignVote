package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint

import java.util.UUID

/**
 * Represents an exception thrown when a player attempts to vote to a vote point
 * which has already been voted by the same player.
 */
class VotePointAlreadyVotedException @java.beans.ConstructorProperties("voterUUID", "votePoint")
constructor(voterUUID: UUID, votePoint: VotePoint) : Exception() {
    var voterUUID: UUID? = null
        internal set
    var votePoint: VotePoint? = null
        internal set

    init {
        this.voterUUID = voterUUID
        this.votePoint = votePoint
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.exception.VotePointAlreadyVotedException(voterUUID=" + this.voterUUID + ", votePoint=" + this.votePoint + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is VotePointAlreadyVotedException) return false
        val other = o as VotePointAlreadyVotedException?
        if (!other!!.canEqual(this as Any)) return false
        val `this$voterUUID` = this.voterUUID
        val `other$voterUUID` = other.voterUUID
        if (if (`this$voterUUID` == null) `other$voterUUID` != null else `this$voterUUID` != `other$voterUUID`) return false
        val `this$votePoint` = this.votePoint
        val `other$votePoint` = other.votePoint
        if (if (`this$votePoint` == null) `other$votePoint` != null else `this$votePoint` != `other$votePoint`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$voterUUID` = this.voterUUID
        result = result * PRIME + (`$voterUUID`?.hashCode() ?: 43)
        val `$votePoint` = this.votePoint
        result = result * PRIME + (`$votePoint`?.hashCode() ?: 43)
        return result
    }

    protected fun canEqual(other: Any): Boolean {
        return other is VotePointAlreadyVotedException
    }
}
