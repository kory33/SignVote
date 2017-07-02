package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.vote.VoteScore;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.entity.Player;

/**
 * Represents an exception thrown when a player attempts to vote with an invalid score
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class InvalidScoreVotedException extends Exception {
    VotePoint targetVotePoint;
    Player voter;
    VoteScore voteScore;
}
