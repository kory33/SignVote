package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.entity.Player

/**
 * Represents an exception thrown when a player attempts to vote with an invalid score
 */
class InvalidScoreVotedException @java.beans.ConstructorProperties("targetVotePoint", "voter", "voteScore")
constructor(targetVotePoint: VotePoint, voter: Player, voteScore: VoteScore) : Exception() {
    var targetVotePoint: VotePoint? = null
        internal set
    var voter: Player? = null
        internal set
    var voteScore: VoteScore? = null
        internal set

    init {
        this.targetVotePoint = targetVotePoint
        this.voter = voter
        this.voteScore = voteScore
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.exception.InvalidScoreVotedException(targetVotePoint=" + this.targetVotePoint + ", voter=" + this.voter + ", voteScore=" + this.voteScore + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is InvalidScoreVotedException) return false
        val other = o as InvalidScoreVotedException?
        if (!other!!.canEqual(this as Any)) return false
        val `this$targetVotePoint` = this.targetVotePoint
        val `other$targetVotePoint` = other.targetVotePoint
        if (if (`this$targetVotePoint` == null) `other$targetVotePoint` != null else `this$targetVotePoint` != `other$targetVotePoint`)
            return false
        val `this$voter` = this.voter
        val `other$voter` = other.voter
        if (if (`this$voter` == null) `other$voter` != null else `this$voter` != `other$voter`) return false
        val `this$voteScore` = this.voteScore
        val `other$voteScore` = other.voteScore
        if (if (`this$voteScore` == null) `other$voteScore` != null else `this$voteScore` != `other$voteScore`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$targetVotePoint` = this.targetVotePoint
        result = result * PRIME + (`$targetVotePoint`?.hashCode() ?: 43)
        val `$voter` = this.voter
        result = result * PRIME + (`$voter`?.hashCode() ?: 43)
        val `$voteScore` = this.voteScore
        result = result * PRIME + (`$voteScore`?.hashCode() ?: 43)
        return result
    }

    protected fun canEqual(other: Any): Boolean {
        return other is InvalidScoreVotedException
    }
}
