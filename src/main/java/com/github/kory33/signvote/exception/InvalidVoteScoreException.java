package com.github.kory33.signvote.exception;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class InvalidVoteScoreException extends Exception {
    @Getter private VotePoint targetVotePoint;
    @Getter private Player voter;
    @Getter private Integer voteScore;
    
    public InvalidVoteScoreException(VotePoint votePoint, Player voter, Integer voteScore) {
        this.targetVotePoint = votePoint;
        this.voter = voter;
        this.voteScore = voteScore;
    }

    private static final long serialVersionUID = -2635407155664972657L;
}