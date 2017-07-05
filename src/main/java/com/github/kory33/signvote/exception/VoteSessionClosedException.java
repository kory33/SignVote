package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.session.VoteSession;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Represents an exception thrown when some interaction is attempted against a closed session.
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class VoteSessionClosedException extends Exception {
    VoteSession session;
}
