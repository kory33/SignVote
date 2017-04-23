package com.github.kory33.signvote.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.constants.Patterns;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VoteManager {
    private final HashMap<UUID, HashMap<Integer, HashSet<String>>> voteData;
    private final VoteSession parentSession;

    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory
     * @throws IOException
     */
    public VoteManager(File voteDataDirectory, VoteSession parentSession) throws IOException {
        this.parentSession = parentSession;
        this.voteData = new HashMap<>();

        if (!voteDataDirectory.isDirectory()) {
            throw new IOException("Directory has to be specified for save location!");
        }

        for (File playerVoteDataFile: voteDataDirectory.listFiles()) {
            JsonObject jsonObject = (new JsonParser()).parse(Files.newBufferedReader(playerVoteDataFile.toPath())).getAsJsonObject();

            Matcher playerUUIDMatcher = Patterns.JSON_FILE_NAME.matcher(playerVoteDataFile.getName());
            if (!playerUUIDMatcher.find()) {
                continue;
            }

            String playerUUID = playerUUIDMatcher.group(1);
            UUID uuid = UUID.fromString(playerUUID);
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            if (!player.hasPlayedBefore()) {
                System.out.println("ignoring" + playerVoteDataFile.getName());
                continue;
            }

            HashMap<Integer, HashSet<String>> votedPointsMap = new HashMap<>();
            jsonObject.entrySet().stream().forEach(entry -> {
                int score = Integer.parseInt(entry.getKey());
                HashSet<String> votePointNameSet = new HashSet<>();
                entry.getValue().getAsJsonArray().forEach(elem -> {
                    votePointNameSet.add(elem.getAsString());
                });
                votedPointsMap.put(score, votePointNameSet);
            });

            this.voteData.put(uuid, votedPointsMap);
        }
    }

    /**
     * Get the players' vote data, as a map of Player to JsonObject
     * @return
     */
    public Map<UUID, JsonObject> getPlayersVoteData() {
        Map<UUID, JsonObject> map = new HashMap<>();
        for (Entry<UUID, HashMap<Integer, HashSet<String>>> playerData: this.voteData.entrySet()) {
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
    public HashMap<Integer, HashSet<String>> getVotedPointsMap(UUID uuid) {
        if (!this.voteData.containsKey(uuid)) {
            this.voteData.put(uuid, new HashMap<>());
        }
        return this.voteData.get(uuid);
    }

    /**
     * Add a vote data related to the score and the votepoint to which the player has voted
     * @param voter
     * @param voteScore
     * @param votePoint
     * @throws IllegalArgumentException when there is a duplicate in the vote
     */
    public void addVotePointData(Player voter, int voteScore, VotePoint votePoint) throws IllegalArgumentException{
        UUID voterUUID = voter.getUniqueId();
        if (!this.voteData.containsKey(voterUUID)) {
            this.voteData.put(voterUUID, new HashMap<>());
        }

        HashMap<Integer, HashSet<String>> votedPointnames = this.voteData.get(voterUUID);
        if (!votedPointnames.containsKey(voteScore)) {
            votedPointnames.put(voteScore, new HashSet<>());
        }

        String votePointName = votePoint.getName();

        if (this.getVotedScore(voter, votePointName).isPresent()) {
            throw new IllegalArgumentException(votePointName + " is already voted by the player!");
        }

        votedPointnames.get(voteScore).add(votePoint.getName());
    }

    public void removeVote(Player player, VotePoint votePoint) throws VotePointNotVotedException {
        HashMap<Integer, HashSet<String>> playerVotes = this.getVotedPointsMap(player.getUniqueId());

        for (Integer voteScore: playerVotes.keySet()) {
            HashSet<String> votedPoints = playerVotes.get(voteScore);
            if (votedPoints.remove(votePoint.getName())) {
                return;
            }
        }

        throw new VotePointNotVotedException(player, votePoint, this.parentSession);
    }

    /**
     * Get the score a given player has voted to a given name of votepoint.
     * The returned optional object contains no value if the player has not voted.
     * @param player
     * @param votePointName
     * @return
     */
    public Optional<Integer> getVotedScore(Player player, String votePointName) {
        return this.getVotedPointsMap(player.getUniqueId()).entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(votePointName))
                .map(entry -> entry.getKey())
                .findFirst();
    }

    public boolean hasVoted(Player player, VotePoint votePoint) {
        return this.getVotedScore(player, votePoint.getName()).isPresent();
    }
}
