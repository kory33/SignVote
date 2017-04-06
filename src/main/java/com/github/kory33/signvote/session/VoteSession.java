package com.github.kory33.signvote.session;

import java.io.File;

import org.bukkit.block.Sign;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.constants.DirectoryPaths;
import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class VoteSession {
    private BijectiveHashMap<Sign, VotePoint> signMap;
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
        for (VotePoint votePoint: signMap.values()) {
            File votePointFile = new File(votePointDirectory, votePoint.getName());
            votePoint.saveTo(votePointFile);
        }
        
        // TODO save other data related to this vote point
    }

    public VotePoint getVotePoint(Sign sign) {
        return this.signMap.get(sign);
    }
}
