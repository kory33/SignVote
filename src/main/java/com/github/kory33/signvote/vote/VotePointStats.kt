package com.github.kory33.signvote.vote

import com.github.kory33.signvote.session.VoteSession

import java.util.Collections
import java.util.HashMap

class VotePointStats(private val voteSession: VoteSession, val votePoint: VotePoint) {

    var scoreDistribution: Map<Int, Int>? = null
        private set

    var totalVotes: Int = 0
        private set
    var totalScores: Int = 0
        private set
    var meanScore: Double = 0.toDouble()
        private set

    var variance: Double = 0.toDouble()
        private set
    var standardDeviation: Double = 0.toDouble()
        private set

    init {
        this.refreshStatsValues()
    }

    /**
     * Calculate the score distribution for the votepoint.
     * @return map of score -> vote count
     */
    private fun computeScoreDistribution(): Map<Int, Int> {
        val distrMap = HashMap<Int, Int>()

        this.voteSession.voteManager.getVotes(this.votePoint)
                .forEach { vote ->
                    val score = vote.score.toInt()
                    val count = distrMap.getOrDefault(score, 0)
                    distrMap.put(score, count + 1)
                }

        return distrMap
    }

    /**
     * Recalculates and refreshes the statistical values within this class
     */
    private fun refreshStatsValues() {
        // initialize distribution cache
        this.scoreDistribution = Collections.unmodifiableMap(this.computeScoreDistribution())

        // compute total votes / scores
        this.totalVotes = 0
        this.totalScores = 0
        this.scoreDistribution!!.forEach { score, count ->
            totalVotes += count
            totalScores += score * count
        }

        if (this.totalVotes == 0) {
            this.meanScore = 0.0
            this.variance = 0.0
            this.standardDeviation = 0.0
        } else {
            // compute mean
            this.meanScore = this.totalScores * 1.0 / this.totalVotes

            // compute variance and standard deviation
            this.variance = 0.0
            this.scoreDistribution!!.forEach { score, count ->
                val deviation = score - this.meanScore
                val sqDeviation = deviation * deviation
                this.variance += sqDeviation * count
            }
            this.variance /= this.totalVotes.toDouble()
            this.standardDeviation = Math.sqrt(variance)
        }
    }
}
