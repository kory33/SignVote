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

import com.github.kory33.signvote.constants.Patterns;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.Vote;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VoteManager {
    private final HashMap<UUID, HashMap<Integer, HashSet<String>>> voteData;
    private final HashMap<VotePoint, HashSet<Vote>> votePointVotes;
    private final VoteSession parentSession;

    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory
     * @throws IOException
     */
    public VoteManager(File voteDataDirectory, VoteSession parentSession) throws IOException {
        this.parentSession = parentSession;
        this.voteData = new HashMap<>();
        this.votePointVotes = new HashMap<>();

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
            if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                System.out.println("ignoring" + playerVoteDataFile.getName());
                continue;
            }

            this.loadPlayerVoteData(uuid, jsonObject);
        }
    }

    private void loadPlayerVoteData(UUID playerUUID, JsonObject jsonObject) {
        HashMap<Integer, HashSet<String>> votedPointsMap = new HashMap<>();
        jsonObject.entrySet().stream().forEach(entry -> {
            int score = Integer.parseInt(entry.getKey());
            HashSet<String> votePointNameSet = new HashSet<>();

            entry.getValue().getAsJsonArray().forEach(elem -> {
                VotePoint votePoint = this.parentSession.getVotePoint(elem.getAsString());
                if (votePoint == null) {
                    return;
                }

                votePointNameSet.add(votePoint.getName());

                if (!votePointVotes.containsKey(votePoint)) {
                    votePointVotes.put(votePoint, new HashSet<>());
                }
                votePointVotes.get(votePoint).add(new Vote(score, playerUUID));
            });
            votedPointsMap.put(score, votePointNameSet);

        });

        this.voteData.put(playerUUID, votedPointsMap);
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
        this.votePointVotes = new HashMap<>();
    }

    /**
     * Get the mapping of voted score to a list of voted points' name from a given player.
     */
    private HashMap<Integer, HashSet<String>> getVotedPointsMap(UUID uuid) {
        if (!this.voteData.containsKey(uuid)) {
            this.voteData.put(uuid, new HashMap<>());
        }
        return this.voteData.get(uuid);
    }

    /**
     * Get the mapping of [vote score] to the [number of times the score has been voted by the player]
     * @param uuid UUID of the player
     * @return A map containing vote scores as keys and vote counts(with the score of corresponding key) as values
     */
    public HashMap<Integer, Integer> getVotedPointsCount(UUID uuid) {
        HashMap<Integer, HashSet<String>> votePointsMap = this.getVotedPointsMap(uuid);

        HashMap<Integer, Integer> voteCounts = new HashMap<>();
        votePointsMap.forEach((score, voteSet) -> voteCounts.put(score, voteSet.size()));

        return voteCounts;
    }

    /**
     * Add a vote data related to the score and the votepoint to which the player has voted
     * @param voterUUID
     * @param voteScore
     * @param votePoint
     * @throws IllegalArgumentException when there is a duplicate in the vote
     */
    public void addVotePointData(UUID voterUUID, int voteScore, VotePoint votePoint) throws VotePointAlreadyVotedException {
        if (!this.voteData.containsKey(voterUUID)) {
            this.voteData.put(voterUUID, new HashMap<>());
        }
        if (!this.votePointVotes.containsKey(votePoint)) {
            this.votePointVotes.put(votePoint, new HashSet<>());
        }

        HashMap<Integer, HashSet<String>> votedPointnames = this.voteData.get(voterUUID);
        if (!votedPointnames.containsKey(voteScore)) {
            votedPointnames.put(voteScore, new HashSet<>());
        }

        String votePointName = votePoint.getName();

        if (this.getVotedScore(voterUUID, votePointName).isPresent()) {
            throw new VotePointAlreadyVotedException(voterUUID, votePoint);
        }

        votedPointnames.get(voteScore).add(votePoint.getName());
        this.votePointVotes.get(votePoint).add(new Vote(voteScore, voterUUID));
    }

    /**
     * Remove a vote casted by the given player to the given votepoint.
     * @param playerUUID
     * @param votePoint
     * @throws VotePointNotVotedException
     */
    public void removeVote(UUID playerUUID, VotePoint votePoint) throws VotePointNotVotedException {
        HashMap<Integer, HashSet<String>> playerVotes = this.getVotedPointsMap(playerUUID);

        for (Integer voteScore: playerVotes.keySet()) {
            HashSet<String> votedPoints = playerVotes.get(voteScore);
            if (votedPoints.remove(votePoint.getName())) {
                break;
            }
        }
        for (Vote vote: this.votePointVotes.get(votePoint)) {
            if (vote.getVoterUuid() == playerUUID) {
                this.votePointVotes.get(votePoint).remove(vote);
                return;
            }
        }

        throw new VotePointNotVotedException(playerUUID, votePoint, this.parentSession);
    }

    /**
     * Get a set of votes casted to the given votepoint.
     * @param votePoint
     * @return
     */
    public HashSet<Vote> getVotes(VotePoint votePoint) {
        if (!this.votePointVotes.containsKey(votePoint)) {
            return new HashSet<>();
        }
        return this.votePointVotes.get(votePoint);
    }

    /**
     * Get the score a given player has voted to a given name of votepoint.
     * The returned optional object contains no value if the player has not voted.
     * @param playerUUID
     * @param votePointName
     * @return
     */
    public Optional<Integer> getVotedScore(UUID playerUUID, String votePointName) {
        return this.getVotedPointsMap(playerUUID).entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(votePointName))
                .map(entry -> entry.getKey())
                .findFirst();
    }

    /**
     * Check if the given player has voted to the specified votepoint.
     * @param playerUUID
     * @param votePoint
     * @return
     */
    public boolean hasVoted(UUID playerUUID, VotePoint votePoint) {
        return this.getVotedScore(playerUUID, votePoint.getName()).isPresent();
    }
}
