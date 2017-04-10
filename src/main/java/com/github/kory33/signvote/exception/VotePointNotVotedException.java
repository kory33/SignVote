package com.github.kory33.signvote.exception;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

public class VotePointNotVotedException extends Exception {
    @Getter Player voter;
    @Getter VotePoint votePoint;
    @Getter VoteSession session;
    
    public VotePointNotVotedException(Player voter, VotePoint votePoint, VoteSession session) {
        this.voter = voter;
        this.votePoint = votePoint;
        this.session = session;
    }

    private static final long serialVersionUID = 3934942422058704536L;
}
