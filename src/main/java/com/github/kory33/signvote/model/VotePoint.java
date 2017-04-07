package com.github.kory33.signvote.model;

import java.io.File;

import org.bukkit.block.Sign;

import com.github.kory33.signvote.session.VoteSession;

import lombok.Getter;

public class VotePoint {
    @Getter private String name;
    @Getter private Sign voteSign;

    private VoteSession parentSession;
    
    public VotePoint(String name, Sign voteSign, VoteSession parentSession) {
        this.name = name;
        this.voteSign = voteSign;
        this.parentSession = parentSession;
    }
    
    public VotePoint(File votePointFIle) throws IllegalArgumentException{
        // TODO implementation
    }
    
    public void saveTo(File file) {
        // TODO implementation
    }
}
