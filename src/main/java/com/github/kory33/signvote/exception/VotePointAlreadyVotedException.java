package com.github.kory33.signvote.exception;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class VotePointAlreadyVotedException extends Exception {
    @Getter Player voter;
    @Getter VotePoint votePoint;
    
    public VotePointAlreadyVotedException(Player voter, VotePoint votePoint) {
        this.voter = voter;
        this.votePoint = votePoint;
    }

    private static final long serialVersionUID = 7563600666472049099L;
}
