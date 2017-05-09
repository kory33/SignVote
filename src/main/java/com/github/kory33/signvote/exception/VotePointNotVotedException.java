package com.github.kory33.signvote.exception;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

/**
 * Represents an exception thrown when a cancel of non-existent vote has been requested.
 */
public class VotePointNotVotedException extends Exception {
    @Getter UUID voterUUID;
    @Getter VotePoint votePoint;
    @Getter VoteSession session;

    /**
     * @deprecated Use {@link #VotePointNotVotedException(UUID, VotePoint, VoteSession)} instead
     */
    public VotePointNotVotedException(Player voter, VotePoint votePoint, VoteSession session) {
        this.voterUUID = voter.getUniqueId();
        this.votePoint = votePoint;
        this.session = session;
    }

    public VotePointNotVotedException(UUID voterUUID, VotePoint votePoint, VoteSession session) {
        this.voterUUID = voterUUID;
        this.votePoint = votePoint;
        this.session = session;
    }

    private static final long serialVersionUID = 3934942422058704536L;
}
