package com.github.kory33.signvote.session;

import java.io.File;
import java.io.IOException;

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
    public VoteSession(File sessionSaveLocation) throws IllegalArgumentException {
        // load all the saved votepoints
        File votePointDirectory = new File(sessionSaveLocation, DirectoryPaths.VOTE_POINTS_DIR);
        for (File votePointFile: votePointDirectory.listFiles()) {
            this.addVotePoint(votePointFile);
        }
    }

    /**
     * Constructs the vote session from its parameters.
     * @param sessionName
     */
    public VoteSession(String sessionName) {
        this.name = sessionName;
    }

    /**
     * Load a votepoint from the existing votepoint data file.
     * @param votePointFIle
     */
    private void addVotePoint(File votePointFIle) {
        try {
            VotePoint votePoint = new VotePoint(votePointFIle);
            this.signMap.put(votePoint.getVoteSign(), votePoint);
        } finally {}
    }

    /**
     * Save the session data to the given directory.
     * @param sessionSaveLocation
     * @throws IOException when the given location is not a directory.
     */
    public void saveTo(File sessionSaveLocation) throws IOException {
        if (!sessionSaveLocation.isDirectory()) {
            throw new IOException("Votesession was about to be saved into a file! (" + sessionSaveLocation.getAbsolutePath() + ")");
        }
        
        File votePointDirectory = new File(sessionSaveLocation, DirectoryPaths.VOTE_POINTS_DIR);
        for (VotePoint votePoint: signMap.values()) {
            File votePointFile = new File(votePointDirectory, votePoint.getName());
            votePoint.saveTo(votePointFile);
        }
        
        // TODO save other data related to this vote point
    }

    /**
     * Get a VotePoint associated with a given Sign.
     * @param sign
     * @return
     */
    public VotePoint getVotePoint(Sign sign) {
        return this.signMap.get(sign);
    }
}
