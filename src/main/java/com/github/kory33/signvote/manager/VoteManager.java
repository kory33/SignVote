package com.github.kory33.signvote.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.constants.Formats;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VoteManager {
    private final HashMap<Player, HashMap<Integer, HashSet<String>>> voteData;
    private final VoteSession parentSession;
    
    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public VoteManager(File voteDataDirectory, VoteSession parentSession) throws IOException {
        this.parentSession = parentSession;
        this.voteData = new HashMap<>();

        if (!voteDataDirectory.isDirectory()) {
            throw new IOException("Directory has to be specified for save location!");
        }
        
        for (File playerVoteDataFile: voteDataDirectory.listFiles()) {
            JsonObject jsonObject = (new JsonParser()).parse(Files.newBufferedReader(playerVoteDataFile.toPath())).getAsJsonObject();
            
            String fileName = playerVoteDataFile.getName();
            String playerUUID = fileName.substring(0, fileName.length() - Formats.JSON_EXT.length() - 1);

            Player player = Bukkit.getPlayer(UUID.fromString(playerUUID));

            if (player == null) {
                continue;
            }
            
            HashMap<Integer, HashSet<String>> votedPointsMap = new HashMap<>();
            this.voteData.put(player, new Gson().fromJson(jsonObject, votedPointsMap.getClass()));
        }
    }
    
    /**
     * Get the players' vote data, as a map of Player to JsonObject
     * @return
     */
    public Map<Player, JsonObject> getPlayersVoteData() {
        Map<Player, JsonObject> map = new HashMap<>();
        for (Entry<Player, HashMap<Integer, HashSet<String>>> playerData: this.voteData.entrySet()) {
            JsonObject jsonObject = new Gson().toJsonTree(playerData.getValue()).getAsJsonObject();
            map.put(playerData.getKey(), jsonObject);
        }
        return map;
    }
    
    /**
     * Construct an empty VoteManager object.
     */
    public VoteManager(VoteSession parentSession) {
        this.voteData = new HashMap<>();
        this.parentSession = parentSession;
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

    public void removeVote(Player player, VotePoint votePoint) throws VotePointNotVotedException {
        HashMap<Integer, HashSet<String>> playerVotes = this.getVotedPointsMap(player);

        for (Integer voteScore: playerVotes.keySet()) {
            HashSet<String> votedPoints = playerVotes.get(voteScore);
            if (votedPoints.remove(votePoint.getName())) {
                return;
            }
        }
        
        throw new VotePointNotVotedException(player, votePoint, this.parentSession);
    }
}
    