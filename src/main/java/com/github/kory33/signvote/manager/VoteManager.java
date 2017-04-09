package com.github.kory33.signvote.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.kory33.signvote.constants.Formats;
import com.github.kory33.signvote.model.VotePoint;

public class VoteManager {
    private HashMap<Player, HashMap<Integer, HashSet<String>>> voteData;
    
    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory
     * @throws IOException
     */
    public VoteManager(File voteDataDirectory) throws IOException {
        this.voteData = new HashMap<>();

        if (!voteDataDirectory.isDirectory()) {
            throw new IOException("Directory has to be specified for save location!");
        }
        
        for (File playerVoteDataFile: voteDataDirectory.listFiles()) {
            String fileContent = String.join("", Files.readAllLines(playerVoteDataFile.toPath()));
            JSONObject jsonObject = new JSONObject(fileContent);
            
            String fileName = playerVoteDataFile.getName();
            String playerUUID = fileName.substring(0, fileName.length() - Formats.JSON_EXT.length() - 1);

            Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));

            if (player == null) {
                continue;
            }
            
            HashMap<Integer, HashSet<String>> votedPointsMap = new HashMap<>();
            
            for (String scoreString: jsonObject.keySet()) {
                try {
                    int score = new Integer(scoreString);
                    HashSet<String> votedVointsNames = new HashSet<>();
                    for (Object object: jsonObject.getJSONArray(scoreString).toList()) {
                        votedVointsNames.add((String)object);
                    }
                    votedPointsMap.put(score, votedVointsNames);
                } catch (Exception e) {
                    continue;
                }
            }

            this.voteData.put(player, votedPointsMap);
        }
    }

    /**
     * Save the vote data under the given directory.
     * @param voteDataDirectory
     * @throws IOException
     */
    public void saveTo(File voteDataDirectory) throws IOException {
        if (!voteDataDirectory.exists()) {
            voteDataDirectory.mkdirs();
        }else if (!voteDataDirectory.isDirectory()) {
            throw new IOException("Directory has to be specified for save location!");
        }
        
        for (Player player: this.voteData.keySet()) {
            File playerVoteDataFile = new File(voteDataDirectory, player.getUniqueId().toString() + Formats.JSON_EXT);
            if (!playerVoteDataFile.exists()) {
                playerVoteDataFile.createNewFile();
            }
            
            JSONObject jsonObject = new JSONObject();
            HashMap<Integer, HashSet<String>> playerVotedPointsMap = this.voteData.get(player);
            
            for (Integer score: playerVotedPointsMap.keySet()) {
                JSONArray votedPointsArray = new JSONArray(playerVotedPointsMap.get(score));
                jsonObject.put(score.toString(), votedPointsArray);
            }
            
            Files.write(playerVoteDataFile.toPath(), jsonObject.toString(4).getBytes(Formats.FILE_ENCODING));
        }
    }
    
    /**
     * Construct an empty VoteManager object.
     */
    public VoteManager() {
        this.voteData = new HashMap<>();
    }
    
    /**
     * Get the mapping of voted score to a list of voted points' name from a given player.
     */
    public HashMap<Integer, HashSet<String>> getVotedPointsMap(Player player) {
        if (!this.voteData.containsKey(player)) {
            this.voteData.put(player, new HashMap<>());
        }
        
        return this.voteData.get(player);
    }

    /**
     * Add the votepoint to which the player has voted
     * @param voter
     * @param votedScore
     * @param votePoint
     * @throws IllegalArgumentException when there is a duplicate in the vote
     */
    public void addVotePointName(Player voter, int votedScore, VotePoint votePoint) throws IllegalArgumentException{
        if (!this.voteData.containsKey(voter)) {
            this.voteData.put(voter, new HashMap<>());
        }
        
        HashMap<Integer, HashSet<String>> votedPointnames = this.voteData.get(voter);
        if (!votedPointnames.containsKey(votedScore)) {
            votedPointnames.put(votedScore, new HashSet<>());
        }
        
        String votePointName = votePoint.getName();
        
        for (int score: votedPointnames.keySet()) {
            if (votedPointnames.get(score).contains(votePointName)) {
                throw new IllegalArgumentException(votePointName + " is already voted by the player!");
            }
        }
        
        votedPointnames.get(votedScore).add(votePoint.getName());
    }
}
    