package com.github.kory33.signvote.manager;

import com.github.kory33.signvote.constants.Patterns;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.Vote;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;

/**
 * A class which handles all the vote data
 */
public class VoteManager {
    private final HashMap<UUID, HashMap<Integer, HashSet<String>>> voteData;
    private final HashMap<VotePoint, HashSet<Vote>> votePointVotes;
    private final VoteSession parentSession;

    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory directory in which vote data is stored player-wise
     * @param parentSession session which is responsible for votes that are about to be read
     * @throws IllegalArgumentException when null value or a non-directory file is given as a parameter
     */
    public VoteManager(File voteDataDirectory, VoteSession parentSession) throws IOException {
        this.parentSession = parentSession;
        this.voteData = new HashMap<>();
        this.votePointVotes = new HashMap<>();

        if (voteDataDirectory == null) {
            throw new IllegalArgumentException("Directory cannot be null!");
        }

        if (!voteDataDirectory.isDirectory()) {
            throw new IOException("Directory has to be specified for save location!");
        }

        File[] dirFiles = voteDataDirectory.listFiles();
        assert dirFiles != null;
        for (File playerVoteDataFile: dirFiles) {
            JsonObject jsonObject = FileUtils.readJSON(playerVoteDataFile);

            Matcher playerUUIDMatcher = Patterns.JSON_FILE_NAME.matcher(playerVoteDataFile.getName());
            if (!playerUUIDMatcher.find()) {
                continue;
            }

            UUID uuid = UUID.fromString(playerUUIDMatcher.group(1));
            this.loadPlayerVoteData(uuid, jsonObject);
        }
    }

    private void loadPlayerVoteData(UUID playerUUID, JsonObject jsonObject) {
        jsonObject.entrySet().forEach(entry -> {
            int score = Integer.parseInt(entry.getKey());

            entry.getValue().getAsJsonArray().forEach(elem -> {
                VotePoint votePoint = this.parentSession.getVotePoint(elem.getAsString());
                if (votePoint == null) {
                    return;
                }

                try {
                    this.addVotePointData(playerUUID, score, votePoint);
                } catch (VotePointAlreadyVotedException ignored) {}
            });
        });
    }

    /**
     * Get the players' vote data, as a map of Player to JsonObject
     * @return mapping of (player's)UUID -> json object containing votes of the player
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
     * @param voterUUID UUID of a player who has voted
     * @param voteScore score which the player has voted
     * @param votePoint vote point to which the player has voted
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
     * Remove a vote casted by the given player to the given vote point.
     * @param playerUUID UUID of a player who tries to cancel the vote
     * @param votePoint vote point whose vote by the player is being cancelled
     * @throws VotePointNotVotedException when the player has not voted to the target vote point
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
            if (vote.getVoterUuid().equals(playerUUID)) {
                this.votePointVotes.get(votePoint).remove(vote);
                return;
            }
        }

        throw new VotePointNotVotedException(playerUUID, votePoint, this.parentSession);
    }

    /**
     * Remove all the votes associated with the given votepoint.
     * @param votePoint vote point from which votes will be removed
     */
    public void removeAllVotes(VotePoint votePoint) {
        HashSet<Vote> votes = this.votePointVotes.get(votePoint);

        // purge votepoint names present in voteData
        votes.forEach(vote -> {
            try {
                this.voteData.get(vote.getVoterUuid()).get(vote.getScore().toInt()).remove(votePoint.getName());
            } catch (NullPointerException exception) {
                // NPE should be thrown If and Only If
                // voteData and votePointVotes are not synchronized correctly
                exception.printStackTrace();
            }
        });

        // clear votePointVotes
        this.votePointVotes.remove(votePoint);
    }

    /**
     * Get a set of votes casted to the given vote point.
     * @param votePoint target vote point
     * @return set containing all the votes casted to the vote point.
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
     * @param playerUUID UUID of the player
     * @param votePointName name of the vote point from which score data is fetched
     * @return an Optional object containing score vote by the player
     */
    public Optional<Integer> getVotedScore(UUID playerUUID, String votePointName) {
        return this.getVotedPointsMap(playerUUID).entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(votePointName))
                .map(Entry::getKey)
                .findFirst();
    }

    /**
     * Check if the given player has voted to the specified votepoint.
     * @param playerUUID UUID of the player
     * @param votePoint target vote point
     * @return boolean value true iff player has voted to the given vote point.
     */
    public boolean hasVoted(UUID playerUUID, VotePoint votePoint) {
        return this.getVotedScore(playerUUID, votePoint.getName()).isPresent();
    }

    /**
     * Refresh the vote point name from the oldName to the newName.
     * This method SHOULD NOT be invoked except from VoteSession.
     * This method MUST be called after change in vote point name to be effective.
     * @param votePoint target vote point(name of this vote point has to be changed beforehand)
     * @param oldName old name of the vote point
     */
    public void refreshVotePointName(VotePoint votePoint, String oldName) {
        HashSet<Vote> votes = this.getVotes(votePoint);
        votes.forEach(vote -> {
            HashSet<String> votedPoints = this.voteData.get(vote.getVoterUuid()).get(vote.getScore().toInt());
            votedPoints.remove(oldName);
            votedPoints.add(votePoint.toString());
        });
    }
}
