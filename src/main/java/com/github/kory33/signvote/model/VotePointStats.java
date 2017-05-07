package com.github.kory33.signvote.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

public class VotePointStats {
    @Getter private final VotePoint votePoint;
    private final VoteSession voteSession;

    @Getter private Map<Integer, Integer> scoreDistribution;

    @Getter private int totalVotes;
    @Getter private int totalScores;
    @Getter private double meanScore;

    @Getter private double variance;
    @Getter private double standardDeviation;

    public VotePointStats(VoteSession session, VotePoint votePoint) {
        this.voteSession = session;
        this.votePoint = votePoint;
        this.refreshStatsValues();
    }

    /**
     * Calculate the score distribution for the votepoint.
     * @return
     */
    private Map<Integer, Integer> computeScoreDistribution() {
        Map<Integer, Integer> distrMap = new HashMap<>();

        this.voteSession.getVoteManager().getVotes(this.votePoint)
            .stream()
            .forEach(vote -> {
                int score = vote.getScore();
                int count = distrMap.getOrDefault(score, 0);
                distrMap.put(score, count + 1);
            });

        return distrMap;
    }

    /**
     * Recalculates and refreshes the statistical values within this class
     */
    public void refreshStatsValues() {
        // initialize distribution cache
        this.scoreDistribution = Collections.unmodifiableMap(this.computeScoreDistribution());

        // compute total votes / scores
        this.totalVotes = 0;
        this.totalScores = 0;
        this.scoreDistribution.forEach((score, count) -> {
            totalVotes += count;
            totalScores += score * count;
        });

        if (this.totalVotes == 0) {
            this.meanScore = 0;
            this.variance = 0;
            this.standardDeviation = 0;
        } else {
            // compute mean
            this.meanScore = this.totalScores * 1.0 / this.totalVotes;

            // compute variance and standard deviation
            this.variance = 0.0d;
            this.scoreDistribution.forEach((score, count) -> {
                double deviation = score - this.meanScore;
                double sqDeviation = deviation * deviation;
                this.variance += sqDeviation * count;
            });
            this.variance /= this.totalVotes;
            this.standardDeviation = Math.sqrt(variance);
        }
    }
}
