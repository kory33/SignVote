package com.github.kory33.signvote.exception;

import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

/**
 * Represents an exception thrown when some interaction is attempted against a closed session.
 */
public class VoteSessionClosedException extends Exception {
    private static final long serialVersionUID = 6177889704963632614L;
    @Getter private final VoteSession session;

    public VoteSessionClosedException(VoteSession voteSession) {
        this.session = voteSession;
    }
}
