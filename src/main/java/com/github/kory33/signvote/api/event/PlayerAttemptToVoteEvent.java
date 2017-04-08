package com.github.kory33.signvote.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

public class PlayerAttemptToVoteEvent implements Cancellable {
    private boolean isCancelled;
    @Getter private Player player;
    @Getter private int voteScore;
    @Getter private VoteSession voteSession;
    @Getter private VotePoint votePoint;
    
    public PlayerAttemptToVoteEvent(Player player, int voteScore, VoteSession session, VotePoint votePoint) {
        this.player = player;
        this.voteScore = voteScore;
        this.voteSession = session;
        this.votePoint = votePoint;
    }
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
