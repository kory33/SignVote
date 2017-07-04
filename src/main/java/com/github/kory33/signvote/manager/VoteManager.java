package com.github.kory33.signvote.manager;

import com.github.kory33.signvote.constants.Patterns;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.utils.FileUtils;
import com.github.kory33.signvote.utils.MapUtil;
import com.github.kory33.signvote.vote.Vote;
import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.vote.VoteScore;
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
    private final UUIDToPlayerVotesMap uuidToPlayerVotesMap;
    private final VotePointVotesCache votePointVotes;
    private final VoteSession parentSession;

    /**
     * Construct a VoteManager object from data at given file location
     * @param voteDataDirectory directory in which vote data is stored player-wise
     * @param parentSession session which is responsible for votes that are about to be read
     * @throws IllegalArgumentException when null value or a non-directory file is given as a parameter
     */
    public VoteManager(File voteDataDirectory, VoteSession parentSession) throws IOException {
        this.parentSession = parentSession;
        this.uuidToPlayerVotesMap = new UUIDToPlayerVotesMap();
        this.votePointVotes = new VotePointVotesCache();

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
            VoteScore score = new VoteScore(Integer.parseInt(entry.getKey()));

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
        return MapUtil.mapValues(this.uuidToPlayerVotesMap, cache -> new Gson().toJsonTree(cache).getAsJsonObject());
    }

    /**
     * Construct an empty VoteManager object.
     */
    public VoteManager(VoteSession parentSession) {
        this.uuidToPlayerVotesMap = new UUIDToPlayerVotesMap();
        this.votePointVotes = new VotePointVotesCache();
        this.parentSession = parentSession;
    }

    /**
     * Get the mapping of voted score to a list of voted points
     */
    private VotesCacheByScores getVotedPointsMap(UUID uuid) {
        return this.uuidToPlayerVotesMap.get(uuid);
    }

    /**
     * Get the mapping of [vote score] to the [number of times the score has been voted by the player]
     * @param uuid UUID of the player
     * @return A map containing vote scores as keys and vote counts(with the score of corresponding key) as values
     */
    public Map<VoteScore, Integer> getVotedPointsCount(UUID uuid) {
        return MapUtil.mapValues(this.getVotedPointsMap(uuid), HashSet::size);
    }

    /**
     * Add a vote data related to the score and the votepoint to which the player has voted
     * @param voterUUID UUID of a player who has voted
     * @param voteScore score which the player has voted
     * @param votePoint vote point to which the player has voted
     * @throws IllegalArgumentException when there is a duplicate in the vote
     */
    public void addVotePointData(UUID voterUUID, VoteScore voteScore, VotePoint votePoint) throws VotePointAlreadyVotedException {
        VotesCacheByScores cacheByScores = this.uuidToPlayerVotesMap.get(voterUUID);

        if (this.getVotedScore(voterUUID, votePoint).isPresent()) {
            throw new VotePointAlreadyVotedException(voterUUID, votePoint);
        }

        cacheByScores.get(voteScore).add(votePoint);
        this.votePointVotes.get(votePoint).add(new Vote(voteScore, voterUUID));
    }

    /**
     * Remove a vote casted by the given player to the given vote point.
     * @param playerUUID UUID of a player who tries to cancel the vote
     * @param votePoint vote point whose vote by the player is being cancelled
     * @throws VotePointNotVotedException when the player has not voted to the target vote point
     */
    public void removeVote(UUID playerUUID, VotePoint votePoint) throws VotePointNotVotedException {
        VotesCacheByScores playerVotes = this.getVotedPointsMap(playerUUID);

        for (VoteScore voteScore: playerVotes.keySet()) {
            HashSet<VotePoint> votedPoints = playerVotes.get(voteScore);
            if (votedPoints.remove(votePoint)) {
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

        // purge votepoint names present in uuidToPlayerVotesMap
        votes.forEach(vote -> {
            try {
                this.uuidToPlayerVotesMap.get(vote.getVoterUuid()).get(vote.getScore()).remove(votePoint);
            } catch (NullPointerException exception) {
                // NPE should be thrown If and Only If
                // uuidToPlayerVotesMap and votePointVotes are not synchronized correctly
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
        return this.votePointVotes.get(votePoint);
    }

    /**
     * Get the score a given player has voted to a given name of votepoint.
     * The returned optional object contains no value if the player has not voted.
     * @param playerUUID UUID of the player
     * @param votePoint vote point from which score data is fetched
     * @return an Optional object containing score vote by the player
     */
    public Optional<Integer> getVotedScore(UUID playerUUID, VotePoint votePoint) {
        return this.getVotedPointsMap(playerUUID).entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(votePoint))
                .map(Entry::getKey)
                .map(VoteScore::toInt)
                .findFirst();
    }

    /**
     * Check if the given player has voted to the specified votepoint.
     * @param playerUUID UUID of the player
     * @param votePoint target vote point
     * @return boolean value true iff player has voted to the given vote point.
     */
    public boolean hasVoted(UUID playerUUID, VotePoint votePoint) {
        return this.getVotedScore(playerUUID, votePoint).isPresent();
    }

    private class VotesCacheByScores extends HashMap<VoteScore, HashSet<VotePoint>> {
        @Override
        public HashSet<VotePoint> get(Object key) {
            if (key instanceof VoteScore && !this.containsKey(key)) {
                this.put((VoteScore) key, new HashSet<>());
            }
            return super.get(key);
        }
    }

    private class UUIDToPlayerVotesMap extends HashMap<UUID, VotesCacheByScores> {
        @Override
        public VotesCacheByScores get(Object key) {
            if (key instanceof UUID && !this.containsKey(key)) {
                this.put((UUID) key, new VotesCacheByScores());
            }
            return super.get(key);
        }
    }

    private class VotePointVotesCache extends HashMap<VotePoint, HashSet<Vote>> {
        @Override
        public HashSet<Vote> get(Object key) {
            if (key instanceof VotePoint && !this.containsKey(key)) {
                this.put((VotePoint) key, new HashSet<>());
            }
            return super.get(key);
        }
    }
}
