package com.github.kory33.signvote.vote

/**
 * Represents a vote's score
 */

data class VoteScore
constructor(private val score: Int) {

    fun toInt(): Int {
        return this.score
    }

    override fun toString(): String {
        return this.score.toString()
    }
}
