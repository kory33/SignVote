package com.github.kory33.signvote.session;

import com.github.kory33.chatgui.util.collection.BijectiveHashMap;
import com.github.kory33.signvote.constants.*;
import com.github.kory33.signvote.exception.*;
import com.github.kory33.signvote.manager.VoteLimitManager;
import com.github.kory33.signvote.manager.VoteManager;
import com.github.kory33.signvote.vote.Limit;
import com.github.kory33.signvote.vote.VotePoint;
import com.github.kory33.signvote.vote.VoteScore;
import com.github.kory33.signvote.utils.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class VoteSession {
    private final BijectiveHashMap<Sign, VotePoint> signMap;
    private final BijectiveHashMap<String, VotePoint> votePointNameMap;

    @Getter final private VoteLimitManager voteLimitManager;
    @Getter final private String name;
    @Getter private final VoteManager voteManager;

    @Setter @Getter private boolean isOpen;

    /**
     * Constructs the vote session from the given session folder
     * @param sessionSaveLocation directory from which session data is read
     * @throws IllegalArgumentException when the session folder is invalid
     */
    public VoteSession(File sessionSaveLocation) throws IllegalArgumentException, IOException {
        this.signMap = new BijectiveHashMap<>();
        this.votePointNameMap = new BijectiveHashMap<>();

        // read information of this vote session
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);

        JsonObject sessionConfigJson = FileUtils.readJSON(sessionDataFile);
        JsonArray voteLimitsJsonArray = sessionConfigJson
                .get(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS)
                .getAsJsonArray();

        this.voteLimitManager = VoteLimitManager.fromJsonArray(voteLimitsJsonArray);
        this.name = sessionConfigJson.get(VoteSessionDataFileKeys.NAME).getAsString();

        this.setOpen(sessionConfigJson.get(VoteSessionDataFileKeys.IS_OPEN).getAsBoolean());

        // load all the saved votepoints
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        File[] votePointFiles = votePointDirectory.listFiles();
        assert votePointFiles != null;
        for (File votePointFile: votePointFiles) {
            this.addVotePoint(votePointFile);
        }

        // initialize vote manager
        this.voteManager = new VoteManager(new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR), this);
    }

    /**
     * Constructs the vote session from its parameters.
     * @param sessionName name of the session
     */
    public VoteSession(String sessionName) {
        this.name = sessionName;

        this.voteLimitManager = new VoteLimitManager();
        this.voteManager = new VoteManager(this);

        this.signMap = new BijectiveHashMap<>();
        this.votePointNameMap = new BijectiveHashMap<>();

        this.setOpen(true);
    }

    /**
     * Load a votepoint from the existing votepoint data file.
     * @param votePointFIle file which contains information about vote point
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
     * add a vote point to the session
     * @param votePoint vote point
     */
    public void addVotePoint(VotePoint votePoint) {
        this.signMap.put(votePoint.getVoteSign(), votePoint);
        this.votePointNameMap.put(votePoint.getName(), votePoint);
    }

    /**
     * Get Json object containing information directly related to this object
     * @return json object containing information about session
     */
    private JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(VoteSessionDataFileKeys.NAME, this.name);
        jsonObject.add(VoteSessionDataFileKeys.VOTE_SCORE_LIMITS, this.voteLimitManager.toJsonArray());
        jsonObject.addProperty(VoteSessionDataFileKeys.IS_OPEN, this.isOpen);

        return jsonObject;
    }

    /**
     * purge non-registered votepoint files under a given directory.
     * @param votePointDirectory directory which contains vote point directory
     */
    private void purgeInvalidVpFiles(File votePointDirectory) {
        Stream<File> nonExistentVpFiles = FileUtils.getFileListStream(votePointDirectory).filter(file -> {
            Matcher matcher = Patterns.JSON_FILE_NAME.matcher(file.getName());
            return !matcher.find() || !this.votePointNameMap.containsKey(matcher.group(1));
        });

        CompletableFuture.runAsync(() -> nonExistentVpFiles.forEach(file -> {
            System.out.println("Deleting " + file.getName());
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }));
    }

    /**
     * Save the session data to the given directory.
     * @param sessionSaveLocation location to which session data is saved
     * @throws IOException when any error occurs whilst saving files
     */
    public void saveTo(File sessionSaveLocation) throws IOException {
        if (!sessionSaveLocation.exists() && !sessionSaveLocation.mkdirs()) {
            throw new IOException("Failed to create vote session save directory!");
        } else if (!sessionSaveLocation.isDirectory()) {
            throw new IOException("Vote session was about to be saved into a file! (" + sessionSaveLocation.getAbsolutePath() + ")");
        }

        // initialize vote point dir
        File votePointDirectory = new File(sessionSaveLocation, FilePaths.VOTE_POINTS_DIR);
        if (!votePointDirectory.exists() && !votePointDirectory.mkdirs()) {
            throw new IOException("Failed to create vote point save directory!");
        }
        this.purgeInvalidVpFiles(votePointDirectory);

        // save vote points
        signMap.getInverse().keySet().stream().parallel()
            .forEach(votePoint -> {
                File saveTarget = new File(votePointDirectory, votePoint.getName() + Formats.JSON_EXT);
                FileUtils.writeJSON(saveTarget, votePoint.toJson());
            });

        // save vote data
        File voteDataDirectory = new File(sessionSaveLocation, FilePaths.VOTE_DATA_DIR);
        if (!voteDataDirectory.exists() && !voteDataDirectory.mkdirs()) {
            throw new IOException("Failed to create vote data directory!");
        }
        this.voteManager.getPlayersVoteData().forEach((playerUUID, voteData) -> {
            File playerVoteDataFile = new File(voteDataDirectory, playerUUID.toString() + Formats.JSON_EXT);

            FileUtils.writeJSON(playerVoteDataFile, voteData);
        });

        // write session data
        File sessionDataFile = new File(sessionSaveLocation, FilePaths.SESSION_DATA_FILENAME);
        JsonObject jsonData = this.toJson();
        FileUtils.writeJSON(sessionDataFile, jsonData);
    }

    /**
     * Get a VotePoint associated with a given Sign.
     * @param sign vote point sign
     * @return vote point instance associated with sign, null if sign is not a vote point
     */
    public VotePoint getVotePoint(Sign sign) {
        return this.signMap.get(sign);
    }

    public VotePoint getVotePoint(String pointName) {
        return this.votePointNameMap.get(pointName);
    }

    /**
     * Rename the votepoint to a given name.
     * @param oldName old name of the vote point
     * @param newName new name of the vote point
     */
    public void renameVotePoint(String oldName, String newName) throws IllegalArgumentException{
        VotePoint target = this.votePointNameMap.get(oldName);
        if (target == null) {
            throw new IllegalArgumentException("No votepoint with that name exists.");
        }

        if (this.votePointNameMap.containsKey(newName)) {
            throw new IllegalArgumentException("A votepoint with name of \"" + newName + "\" already exists.");
        }

        target.setName(newName);
        voteManager.refreshVotePointName(target, oldName);
    }

    /**
     * Get a score -> count map of available votes for a given player
     * @param player player instance
     * @return map of score -> reserved limit
     * limit is an optional with limit value, empty if infinite number of votes can be casted
     */
    public Map<VoteScore, Limit> getAvailableVoteCounts(Player player) {
        Map<VoteScore, Limit> availableCounts = this.getReservedVoteCounts(player);
        Map<VoteScore, Integer> votedScoreCounts = this.voteManager.getVotedPointsCount(player.getUniqueId());

        votedScoreCounts.forEach((score, votedNum) -> {
            if (!availableCounts.containsKey(score)) {
                return;
            }

            Limit reservedVotes = availableCounts.remove(score);
            Limit remainingVotes = reservedVotes.minus(votedNum);

            // iff remaining != 0
            if (!remainingVotes.isZero()) {
                availableCounts.put(score, remainingVotes);
            }
        });

        return availableCounts;
    }

    /**
     * Get a score -> count map of reserved votes for a given player
     * @param player player instance
     * @return map of score -> available limit
     * limit is an optional with limit value, empty if infinite number of votes can be casted
     */
    public Map<VoteScore, Limit> getReservedVoteCounts(Player player) {
        return this.voteLimitManager.getLimitSet(player);
    }

    /**
     * Make a vote to the specified votepoint with a given score.
     * Score has to be checked for it's validity,
     * but may not be checked for player vote limits as an exception is thrown
     * @param player player who is attempting to vote
     * @param votePoint vote point to which the player is attempting to vote
     * @param voteScore score of vote which the player is attempting to cast
     *
     * @throws ScoreCountLimitReachedException when the player can no longer vote with the given score due to the limit
     * @throws VotePointAlreadyVotedException when the player has already voted to the votepoint
     * @throws VoteSessionClosedException when this vote session is closed
     */
    public void vote(Player player, VotePoint votePoint, VoteScore voteScore)
            throws ScoreCountLimitReachedException, VotePointAlreadyVotedException, InvalidScoreVotedException, VoteSessionClosedException {
        if (this.voteLimitManager.getLimit(voteScore, player).isZero()) {
            throw new InvalidScoreVotedException(votePoint, player, voteScore);
        }

        if (!this.getReservedVoteCounts(player).containsKey(voteScore)) {
            throw new ScoreCountLimitReachedException(player, votePoint, voteScore);
        }

        if (!this.getAvailableVoteCounts(player).containsKey(voteScore)) {
            throw new ScoreCountLimitReachedException(player, votePoint, voteScore);
        }

        if (!this.isOpen()) {
            throw new VoteSessionClosedException(this);
        }

        this.voteManager.addVotePointData(player.getUniqueId(), voteScore.toInt(), votePoint);
    }

    /**
     * Cancel a vote to the specified votepoint made by a given player.
     * @param player player who is trying to cancel a vote
     * @param votePoint vote point from which the vote should be removed
     * @throws VotePointNotVotedException When the player hasn't voted the votepoint.
     */
    public void unvote(Player player, VotePoint votePoint) throws VotePointNotVotedException {
        this.voteManager.removeVote(player.getUniqueId(), votePoint);
    }

    /**
     * Delete the specified votepoint
     * @param votePoint target vote point
     */
    public void deleteVotepoint(VotePoint votePoint) {
        this.voteManager.removeAllVotes(votePoint);

        this.votePointNameMap.removeValue(votePoint);
        Sign sign = this.signMap.removeValue(votePoint);

        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT);
        sign.setLine(1, SignTexts.DELETED);
        sign.setLine(2, "");
        sign.update();
    }

    /**
     * Get all the votepoints registered to this vote session
     */
    public Set<VotePoint> getAllVotePoints() {
        return this.votePointNameMap.getInverse().keySet();
    }
}
