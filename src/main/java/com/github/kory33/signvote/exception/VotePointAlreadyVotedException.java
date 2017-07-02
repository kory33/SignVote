package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.model.VotePoint;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

/**
 * Represents an exception thrown when a player attempts to vote to a vote point
 * which has already been voted by the same player.
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class VotePointAlreadyVotedException extends Exception {
    UUID voterUUID;
    VotePoint votePoint;
}
