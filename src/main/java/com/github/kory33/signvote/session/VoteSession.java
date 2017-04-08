package com.github.kory33.signvote.session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.collection.VoteScoreLimits;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.FilePaths;
import com.github.kory33.signvote.constants.Formats;
import com.github.kory33.signvote.constants.VoteSessionDataFileKeys;
import com.github.kory33.signvote.manager.VoteManager;
import com.github.kory33.signvote.model.VotePoint;

import lombok.Getter;
import lombok.Setter;

public class VoteSession {
    private final BijectiveHashMap<Sign, VotePoint> signMap;
    private final BijectiveHashMap<String, VotePoint> votePointNameMap;

    @Getter final private VoteScoreLimits voteScoreCountLimits;
    @Getter final private String name;
    @Getter private final VoteManager voteManager;
    
    @Setter @Getter private boolean isOpen;
    
    /**
     * Constructs the vote session from the given session folder
     * @param sessionFolder
     * @throws IllegalArgumentException when the session folder is invalid
     * @throws IOException 
     */
    public VoteSession(File sessionSaveLocation) throws IllegalArgumentException, IOException {
        this.signMap = new BijectiveHashMap<>();
        this.votePointNameMap = new BijectiveHashMap<>();
        
        // load all the saved votepoints
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        for (File votePointFile: votePointDirectory.listFiles()) {
            this.addVotePoint(votePointFile);
        }

        this.voteManager = new VoteManager(new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR));
        
        // read information of this vote session
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);

        JSONObject sessionConfigJson = (new JSONConfiguration(sessionDataFile)).getJsonObject();
        JSONObject voteLimits = sessionConfigJson.getJSONObject(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS);

        this.voteScoreCountLimits = new VoteScoreLimits(voteLimits);
        this.name = sessionConfigJson.getString(VoteSessionDataFileKeys.NAME);

        this.setOpen(sessionConfigJson.getBoolean(VoteSessionDataFileKeys.IS_OPEN));
    }
    
    /**
     * Constructs the vote session from its parameters.
     * @param sessionName
     */
    public VoteSession(String sessionName) {
        this.name = sessionName;

        this.voteScoreCountLimits = new VoteScoreLimits();
        this.voteManager = new VoteManager();
        
        this.signMap = new BijectiveHashMap<>();
        this.votePointNameMap = new BijectiveHashMap<>();
        
        this.setOpen(true);
    }

    /**
     * Load a votepoint from the existing votepoint data file.
     * @param votePointFIle
     */
    private void addVotePoint(File votePointFIle) {
        try {
            VotePoint votePoint = new VotePoint(votePointFIle);
            this.addVotePoint(votePoint);
        } catch (Exception e) {}
    }

    /**
     * 
     * @param votePoint
     */
    public void addVotePoint(VotePoint votePoint) {
        this.signMap.put(votePoint.getVoteSign(), votePoint);
        this.votePointNameMap.put(votePoint.getName(), votePoint);
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
            File votePointFile = new File(votePointDirectory, votePoint.getName() + Formats.JSON_EXT);
            if (!votePointFile.exists()) {
                votePointFile.createNewFile();
            }
            
            Files.write(votePointFile.toPath(), votePoint.toJson().toString(4).getBytes(Formats.FILE_ENCODING));
        }

        this.voteManager.saveTo(new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR));
        
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);
        if (!sessionDataFile.exists()) {
            sessionDataFile.createNewFile();
        }
        
        Files.write(sessionDataFile.toPath(), this.toJson().toString(4).getBytes(Formats.FILE_ENCODING));
    }

    /**
     * Get a VotePoint associated with a given Sign.
     * @param sign
     * @return
     */
    public VotePoint getVotePoint(Sign sign) {
        return this.signMap.get(sign);
    }
    
    public VotePoint getVotePoint(String pointName) {
        return this.votePointNameMap.get(pointName);
    }
    
    /**
     * Get a score -> count map of available votes for a given player
     * @param player
     * @return
     */
    public HashMap<Integer, Integer> getAvailableVoteCounts(Player player) {
        HashMap<Integer, Integer> availableCounts = this.getReservedVoteCounts(player);
        
        HashMap<Integer, HashSet<String>> votedScores = this.voteManager.getVotedPointsMap(player);
        for (int score: votedScores.keySet()) {
            int votedNum = votedScores.get(score).size();
            
            if (!availableCounts.containsKey(score)) {
                continue;
            }
            
            int reservedVotes = availableCounts.remove(score);
            int remainingVotes = reservedVotes - votedNum;
            
            if (remainingVotes <= 0) {
                continue;
            }
            
            availableCounts.put(score, remainingVotes);
        }
        
        return availableCounts;
    }
    
    /**
     * Get a score -> count map of reserved votes for a given player
     * @param player
     * @return
     */
    public HashMap<Integer, Integer> getReservedVoteCounts(Player player) {
        HashMap<Integer, Integer> reservedCounts = new HashMap<>();
        
        for (int score: this.voteScoreCountLimits.getVotableScores()) {
            reservedCounts.put(score, this.voteScoreCountLimits.getLimit(score, player));
        }
        
        return reservedCounts;
    }
}
