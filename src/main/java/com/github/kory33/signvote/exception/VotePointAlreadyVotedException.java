package com.github.kory33.signvote.exception;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class VotePointAlreadyVotedException extends Exception {
    @Getter UUID voterUUID;
    @Getter VotePoint votePoint;

    /**
     * @deprecated Use {@link #VotePointAlreadyVotedException(UUID, VotePoint)} instead.
     * @param voter
     * @param votePoint
     */
    public VotePointAlreadyVotedException(Player voter, VotePoint votePoint) {
        this.voterUUID = voter.getUniqueId();
        this.votePoint = votePoint;
    }

    public VotePointAlreadyVotedException(UUID voterUUID, VotePoint votePoint) {
        this.voterUUID = voterUUID;
        this.votePoint = votePoint;
    }

    private static final long serialVersionUID = 7563600666472049099L;
}
