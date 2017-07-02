package com.github.kory33.signvote.vote;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Represents a vote's score
 */

@RequiredArgsConstructor
@EqualsAndHashCode
public final class VoteScore {
    private final int score;

    public int toInt() {
        return this.score;
    }
}
