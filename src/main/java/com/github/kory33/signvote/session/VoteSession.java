package com.github.kory33.signvote.session;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.collection.VoteScoreLimits;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.FilePaths;
import com.github.kory33.signvote.constants.Formats;
import com.github.kory33.signvote.constants.MagicNumbers;
import com.github.kory33.signvote.constants.Patterns;
import com.github.kory33.signvote.constants.SignTexts;
import com.github.kory33.signvote.constants.VoteSessionDataFileKeys;
import com.github.kory33.signvote.exception.InvalidVoteScoreException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.manager.VoteManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.utils.FileUtils;
import com.google.gson.JsonObject;

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

        this.voteManager = new VoteManager(new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR), this);

        // read information of this vote session
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);

        JsonObject sessionConfigJson = (new JSONConfiguration(sessionDataFile)).getJsonObject();
        JsonObject voteLimits = sessionConfigJson.get(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS).getAsJsonObject();

        this.voteScoreCountLimits = new VoteScoreLimits(voteLimits);
        this.name = sessionConfigJson.get(VoteSessionDataFileKeys.NAME).getAsString();

        this.setOpen(sessionConfigJson.get(VoteSessionDataFileKeys.IS_OPEN).getAsBoolean());
    }

    /**
     * Constructs the vote session from its parameters.
     * @param sessionName
     */
    public VoteSession(String sessionName) {
        this.name = sessionName;

        this.voteScoreCountLimits = new VoteScoreLimits();
        this.voteManager = new VoteManager(this);

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
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "", e);
        }
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
    private JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(VoteSessionDataFileKeys.NAME, this.name);
        jsonObject.add(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS, this.voteScoreCountLimits.toJson());
        jsonObject.addProperty(VoteSessionDataFileKeys.IS_OPEN, this.isOpen);

        return jsonObject;
    }

    /**
     * purge non-registered votepoint files under a given directory.
     * @param votePointDirectory
     */
    private void purgeInvalidVpFiles(File votePointDirectory) {
        Stream<File> nonExistentVpFiles = FileUtils.getFileListStream(votePointDirectory).filter(file -> {
                Matcher matcher = Patterns.JSON_FILE_NAME.matcher(file.getName());
                if (!matcher.find()) {
                    return true;
                }
                return !this.votePointNameMap.containsKey(matcher.group(1));
            });

        CompletableFuture.runAsync(() -> nonExistentVpFiles.forEach(file -> {
            System.out.println("Deleting " + file.getName());
            file.delete();
        }));
    }

    /**
     * Save the session data to the given directory.
     * @param sessionSaveLocation
     * @throws IOException when the given location is not a directory.
     */
    public void saveTo(File sessionSaveLocation) throws IOException {
        if (!sessionSaveLocation.exists()) {
            sessionSaveLocation.mkdirs();
        } else if (!sessionSaveLocation.isDirectory()) {
            throw new IOException("Votesession was about to be saved into a file! (" + sessionSaveLocation.getAbsolutePath() + ")");
        }

        // initialize votepoint dir
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        if (!votePointDirectory.exists()) {
            votePointDirectory.mkdirs();
        }
        this.purgeInvalidVpFiles(votePointDirectory);

        // save votepoints
        signMap.getInverse().keySet().stream().parallel()
            .forEach(votePoint -> {
                File saveTarget = new File(votePointDirectory, votePoint.getName() + Formats.JSON_EXT);
                FileUtils.writeJSON(saveTarget, votePoint.toJson());
            });

        // save vote data
        File voteDataDirectory = new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR);
        if (!voteDataDirectory.exists()) {
            voteDataDirectory.mkdirs();
        }
        this.voteManager.getPlayersVoteData().entrySet().stream()
            .forEach(entry -> {
                UUID playerUUID = entry.getKey();
                File playerVoteDataFile = new File(voteDataDirectory, playerUUID.toString() + Formats.JSON_EXT);

                JsonObject voteData = entry.getValue();
                FileUtils.writeJSON(playerVoteDataFile, voteData);
            });

        // write session data
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);
        JsonObject jsonData = this.toJson();
        FileUtils.writeJSON(sessionDataFile, jsonData);
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
    public HashMap<Integer, Optional<Integer>> getAvailableVoteCounts(Player player) {
        HashMap<Integer, Optional<Integer>> availableCounts = this.getReservedVoteCounts(player);

        HashMap<Integer, HashSet<String>> votedScores = this.voteManager.getVotedPointsMap(player.getUniqueId());
        for (int score: votedScores.keySet()) {
            int votedNum = votedScores.get(score).size();

            if (!availableCounts.containsKey(score)) {
                continue;
            }

            Optional<Integer> reservedVotes = availableCounts.remove(score);

            // infinity votes(empty Optional) remains infinity, otherwise Max(reserved - voted) is remaining
            Optional<Integer> remainingVotes = reservedVotes.map(res -> Math.max(res - votedNum, 0));

            // iff remaining != 0
            if (remainingVotes.orElse(-1) != 0) {
                availableCounts.put(score, remainingVotes);
            }
        }

        return availableCounts;
    }

    /**
     * Get a score -> count map of reserved votes for a given player
     * @param player
     * @return
     */
    public HashMap<Integer, Optional<Integer>> getReservedVoteCounts(Player player) {
        HashMap<Integer, Optional<Integer>> reservedCounts = new HashMap<>();

        for (int score: this.voteScoreCountLimits.getVotableScores()) {
            int limit = this.voteScoreCountLimits.getLimit(score, player);
            if (limit == MagicNumbers.VOTELIMIT_INFINITY) {
                reservedCounts.put(score, Optional.empty());
            } else {
                reservedCounts.put(score, Optional.of(limit));
            }
        }

        return reservedCounts;
    }

    /**
     * Make a vote to the specified votepoint with a given score.
     * Score has to be checked for it's validity,
     * but may not be checked for player vote limits as an exception is thrown
     * @param player
     * @param votePoint
     * @param voteScore
     *
     * @throws ScoreCountLimitReachedException when the player can no longer vote with the given score due to the limit
     * @throws VotePointAlreadyVotedException when the player has already voted to the votepoint
     */
    public void vote(Player player, VotePoint votePoint, int voteScore)
            throws ScoreCountLimitReachedException, VotePointAlreadyVotedException, InvalidVoteScoreException {
        if (this.voteScoreCountLimits.getLimit(voteScore, player) == 0) {
            throw new InvalidVoteScoreException(votePoint, player, voteScore);
        }

        if (!this.getReservedVoteCounts(player).containsKey(voteScore)) {
            throw new ScoreCountLimitReachedException(player, votePoint, voteScore);
        }

        if (!this.getAvailableVoteCounts(player).containsKey(voteScore)) {
            throw new ScoreCountLimitReachedException(player, votePoint, voteScore);
        }

        this.voteManager.addVotePointData(player, voteScore, votePoint);
    }

    /**
     * Cancel a vote to the specified votepoint made by a given player.
     * @param player
     * @param votePoint
     * @throws VotePointNotVotedException When the player hasn't voted the votepoint.
     */
    public void unvote(Player player, VotePoint votePoint) throws VotePointNotVotedException {
        this.voteManager.removeVote(player, votePoint);
    }

    /**
     * Delete the specified votepoint
     * @param votePoint
     */
    public void deleteVotepoint(VotePoint votePoint) {
        this.votePointNameMap.removeValue(votePoint);
        Sign sign = this.signMap.removeValue(votePoint);

        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT);
        sign.setLine(1, SignTexts.DELETED);
        sign.setLine(2, "");
        sign.update();

        return;
    }
}
