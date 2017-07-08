package com.github.kory33.signvote.exception

import com.github.kory33.signvote.vote.VotePoint
import com.github.kory33.signvote.vote.VoteScore
import org.bukkit.entity.Player

/**
 * Represents an exception thrown when a player attempts to vote with a score
 * whose vote limit has already been reached.
 */
class ScoreCountLimitReachedException @java.beans.ConstructorProperties("player", "voteTarget", "voteScore")
constructor(player: Player, voteTarget: VotePoint, voteScore: VoteScore) : Exception() {
    var player: Player? = null
        internal set
    var voteTarget: VotePoint? = null
        internal set
    var voteScore: VoteScore? = null
        internal set

    init {
        this.player = player
        this.voteTarget = voteTarget
        this.voteScore = voteScore
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.exception.ScoreCountLimitReachedException(player=" + this.player + ", voteTarget=" + this.voteTarget + ", voteScore=" + this.voteScore + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is ScoreCountLimitReachedException) return false
        val other = o as ScoreCountLimitReachedException?
        if (!other!!.canEqual(this as Any)) return false
        val `this$player` = this.player
        val `other$player` = other.player
        if (if (`this$player` == null) `other$player` != null else `this$player` != `other$player`) return false
        val `this$voteTarget` = this.voteTarget
        val `other$voteTarget` = other.voteTarget
        if (if (`this$voteTarget` == null) `other$voteTarget` != null else `this$voteTarget` != `other$voteTarget`)
            return false
        val `this$voteScore` = this.voteScore
        val `other$voteScore` = other.voteScore
        if (if (`this$voteScore` == null) `other$voteScore` != null else `this$voteScore` != `other$voteScore`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$player` = this.player
        result = result * PRIME + (`$player`?.hashCode() ?: 43)
        val `$voteTarget` = this.voteTarget
        result = result * PRIME + (`$voteTarget`?.hashCode() ?: 43)
        val `$voteScore` = this.voteScore
        result = result * PRIME + (`$voteScore`?.hashCode() ?: 43)
        return result
    }

    protected fun canEqual(other: Any): Boolean {
        return other is ScoreCountLimitReachedException
    }
}
