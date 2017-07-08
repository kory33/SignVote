package com.github.kory33.signvote.vote

import java.util.UUID

/**
 * Abstract representation of a vote.
 * @author Kory
 */
class Vote @java.beans.ConstructorProperties("score", "voterUuid")
constructor(val score: VoteScore, val voterUuid: UUID) {

    constructor(score: Int, voterUuid: UUID) : this(VoteScore(score), voterUuid)

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is Vote) return false
        val other = o as Vote?
        val `this$score` = this.score
        val `other$score` = other!!.score
        if (if (`this$score` == null) `other$score` != null else `this$score` != `other$score`) return false
        val `this$voterUuid` = this.voterUuid
        val `other$voterUuid` = other.voterUuid
        if (if (`this$voterUuid` == null) `other$voterUuid` != null else `this$voterUuid` != `other$voterUuid`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$score` = this.score
        result = result * PRIME + `$score`.hashCode()
        val `$voterUuid` = this.voterUuid
        result = result * PRIME + `$voterUuid`.hashCode()
        return result
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.vote.Vote(score=" + this.score + ", voterUuid=" + this.voterUuid + ")"
    }
}
