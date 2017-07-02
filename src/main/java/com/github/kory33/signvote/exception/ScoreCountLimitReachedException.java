package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.vote.VoteScore;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.entity.Player;

/**
 * Represents an exception thrown when a player attempts to vote with a score
 * whose vote limit has already been reached.
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class ScoreCountLimitReachedException extends Exception {
    Player player;
    VotePoint voteTarget;
    VoteScore voteScore;
}
