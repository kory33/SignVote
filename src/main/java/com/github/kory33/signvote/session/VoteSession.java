package com.github.kory33.signvote.session;

import java.io.File;
import java.util.ArrayList;

import com.github.kory33.signvote.constants.DirectoryPaths;
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
        // TODO implement reading from session folder
    }
    
    public VoteSession(String sessionName) {
        this.name = sessionName;
    }
    
    public void saveTo(File sessionSaveLocation) {
        File votePointDirectory = new File(sessionSaveLocation, DirectoryPaths.VOTE_POINTS_DIR);
        for (VotePoint votePoint: votePoints) {
            File votePointFile = new File(votePointDirectory, votePoint.getName());
            votePoint.saveTo(votePointFile);
        }
        
        // TODO save other data related to this vote point
    }
}
