package com.github.kory33.signvote.exception

import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.vote.VotePoint

import java.util.UUID

/**
 * Represents an exception thrown when a cancel of non-existent vote has been requested.
 */
class VotePointNotVotedException @java.beans.ConstructorProperties("voterUUID", "votePoint", "session")
constructor(voterUUID: UUID, votePoint: VotePoint, session: VoteSession) : Exception() {
    var voterUUID: UUID? = null
        internal set
    var votePoint: VotePoint? = null
        internal set
    var session: VoteSession? = null
        internal set

    init {
        this.voterUUID = voterUUID
        this.votePoint = votePoint
        this.session = session
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.exception.VotePointNotVotedException(voterUUID=" + this.voterUUID + ", votePoint=" + this.votePoint + ", session=" + this.session + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is VotePointNotVotedException) return false
        val other = o as VotePointNotVotedException?
        if (!other!!.canEqual(this as Any)) return false
        val `this$voterUUID` = this.voterUUID
        val `other$voterUUID` = other.voterUUID
        if (if (`this$voterUUID` == null) `other$voterUUID` != null else `this$voterUUID` != `other$voterUUID`) return false
        val `this$votePoint` = this.votePoint
        val `other$votePoint` = other.votePoint
        if (if (`this$votePoint` == null) `other$votePoint` != null else `this$votePoint` != `other$votePoint`) return false
        val `this$session` = this.session
        val `other$session` = other.session
        if (if (`this$session` == null) `other$session` != null else `this$session` != `other$session`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$voterUUID` = this.voterUUID
        result = result * PRIME + (`$voterUUID`?.hashCode() ?: 43)
        val `$votePoint` = this.votePoint
        result = result * PRIME + (`$votePoint`?.hashCode() ?: 43)
        val `$session` = this.session
        result = result * PRIME + (`$session`?.hashCode() ?: 43)
        return result
    }

    protected fun canEqual(other: Any): Boolean {
        return other is VotePointNotVotedException
    }
}
