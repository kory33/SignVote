package com.github.kory33.signvote.exception;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

/**
 * Represents an exception thrown when a player attempts to vote with an invalid score
 */
public class InvalidScoreVotedException extends Exception {
    @Getter private VotePoint targetVotePoint;
    @Getter private Player voter;
    @Getter private Integer voteScore;
    
    public InvalidScoreVotedException(VotePoint votePoint, Player voter, Integer voteScore) {
        this.targetVotePoint = votePoint;
        this.voter = voter;
        this.voteScore = voteScore;
    }

    private static final long serialVersionUID = -2635407155664972657L;
}
