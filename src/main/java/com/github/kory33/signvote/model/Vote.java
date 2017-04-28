package com.github.kory33.signvote.model;

import java.util.UUID;

import lombok.Getter;

/**
 * Abstract representation of a vote.
 * @author Kory
 */
public class Vote {
    @Getter private final int score;
    @Getter private final UUID voterUuid;

    public Vote(int score, UUID voterUuid) {
        this.score = score;
        this.voterUuid = voterUuid;
    }
}
