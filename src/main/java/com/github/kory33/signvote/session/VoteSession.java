package com.github.kory33.signvote.session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.block.Sign;
import org.json.JSONObject;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.collection.VoteScoreLimits;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.FilePaths;
import com.github.kory33.signvote.constants.VoteSessionDataFileKeys;
import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;

public class VoteSession {
    private BijectiveHashMap<Sign, VotePoint> signMap;

    private VoteScoreLimits voteScoreCountLimits;
    @Getter private String name;
    
    /**
     * Constructs the vote session from the given session folder
     * @param sessionFolder
     * @throws IllegalArgumentException when the session folder is invalid
     * @throws IOException 
     */
    public VoteSession(File sessionSaveLocation) throws IllegalArgumentException, IOException {
        // load all the saved votepoints
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        for (File votePointFile: votePointDirectory.listFiles()) {
            this.addVotePoint(votePointFile);
        }

        // read information of this vote session
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);

        JSONObject sessionConfigJson = (new JSONConfiguration(sessionDataFile)).getJsonObject();
        JSONObject voteLimits = sessionConfigJson.getJSONObject(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS);

        this.voteScoreCountLimits = new VoteScoreLimits(voteLimits);
        this.name = sessionConfigJson.getString(VoteSessionDataFileKeys.NAME);
    }

    /**
     * 
     * @param score The score whose count limit is to be set
     * @param permissionNode Corresponding permission node for the limit.
     * null or "default" should be given for default count limit.
     * @param limit The limit of the count for two previously specified parameters
     * @throws IllegalArgumentException when the score count limit already exists in the session.
     */
    public void addVoteScoreCountLimit(int score, String _permissionNode, int limit) throws IllegalArgumentException {
        String permissionNode = _permissionNode;
        if (permissionNode == null) {
            permissionNode = "default";
        }

        this.voteScoreCountLimits.addLimit(score, permissionNode, limit);
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
     * 
     * @param votePoint
     */
    public void addVotePoint(VotePoint votePoint) {
        this.signMap.put(votePoint.getVoteSign(), votePoint);
    }
    
    /**
     * Get Json object containing information directly related to this object
     * @return
     */
    private JSONObject toJson() {
        JSONObject sessionData = new JSONObject();
        
        sessionData.put(VoteSessionDataFileKeys.NAME, this.name);
        sessionData.put(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS, this.voteScoreCountLimits.toJson());
        
        return sessionData;
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
        
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        for (VotePoint votePoint: signMap.values()) {
            File votePointFile = new File(votePointDirectory, votePoint.getName());
            votePoint.saveTo(votePointFile);
        }

        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);
        if (!sessionDataFile.exists()) {
            sessionDataFile.createNewFile();
        }
        
        Files.write(sessionDataFile.toPath(), this.toJson().toString(4).getBytes("utf-8"));
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
