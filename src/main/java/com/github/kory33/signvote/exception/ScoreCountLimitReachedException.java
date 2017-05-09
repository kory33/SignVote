package com.github.kory33.signvote.exception;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

/**
 * Represents an exception thrown when a player attempts to vote with a score
 * whose vote limit has already been reached.
 */
public class ScoreCountLimitReachedException extends Exception {
    @Getter private Player player;
    @Getter private VotePoint voteTarget;
    @Getter private int voteScore;
    
    public ScoreCountLimitReachedException(Player player, VotePoint voteTarget, int voteScore) {
        this.player = player;
        this.voteTarget = voteTarget;
        this.voteScore = voteScore;
    }

    private static final long serialVersionUID = 8326660259946013401L;
    
}
