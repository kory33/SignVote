package com.github.kory33.signvote.vote;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Abstract representation of a vote.
 * @author Kory
 */
@Data
@RequiredArgsConstructor
public final class Vote {
    private final VoteScore score;
    private final UUID voterUuid;

    public Vote(int score, UUID voterUuid) {
        this(new VoteScore(score), voterUuid);
    }
}
