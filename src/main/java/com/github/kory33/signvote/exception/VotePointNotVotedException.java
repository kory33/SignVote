package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

/**
 * Represents an exception thrown when a cancel of non-existent vote has been requested.
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class VotePointNotVotedException extends Exception {
    UUID voterUUID;
    VotePoint votePoint;
    VoteSession session;
}
