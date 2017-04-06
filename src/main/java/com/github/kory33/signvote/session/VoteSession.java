package com.github.kory33.signvote.session;

import java.io.File;
import java.util.ArrayList;

import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class VoteSession {
    private ArrayList<VotePoint> votePoints;
    @Getter private String name;
    
    /**
     * Constructs the vote session from the given session folder
     * @param sessionFolder
     * @throws IllegalArgumentException when the session folder is invalid
     */
    public VoteSession(File sessionFolder) throws IllegalArgumentException {
        // TODO implementations
    }
}
