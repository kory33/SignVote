package com.github.kory33.signvote.manager;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import com.github.kory33.signvote.vote.Limit;
import com.github.kory33.signvote.vote.VoteLimit;
import com.github.kory33.signvote.vote.VoteScore;
import com.google.gson.JsonArray;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class that manages a collection of vote-limits
 */
public class VoteLimitManager {
    private final Set<VoteLimit> limitSet;

    public VoteLimitManager() {
        this.limitSet = new HashSet<>();
    }

    public void addVoteLimit(VoteLimit voteLimit) {
        this.limitSet.add(voteLimit);
    }

    /**
     * Convert this object into a json array
     * @return converted json array
     */
    public JsonArray toJsonArray() {
        JsonArray resultArray = new JsonArray();
        this.limitSet.forEach(limit -> resultArray.add(limit.toJsonObject()));
        
        return resultArray;
    }

    /**
     * Construct a {@link VoteLimitManager} object from a json object.
     *
     * <p>
     * Any object in the array which does not conform to the format of VoteLimit object will be ignored.
     * </p>
     * @param voteLimitsJsonArray json array from which the data will be read
     * @return constructed manager object
     */
    public static VoteLimitManager fromJsonArray(JsonArray voteLimitsJsonArray) {
        VoteLimitManager voteLimitManager = new VoteLimitManager();

        voteLimitsJsonArray.forEach(element -> {
            try {
                voteLimitManager.addVoteLimit(VoteLimit.fromJsonObject(element.getAsJsonObject()));
            } catch (InvalidLimitDataException e) {
                System.out.print("Ignoring json element : " + element.toString());
            }
        });

        return voteLimitManager;
    }

    /**
     * Get a set of all the scores that may be cast by an OP player.
     * @return set containing {@link VoteScore} object
     */
    public Set<VoteScore> getVotableScores() {
        return this.limitSet.stream()
                .map(VoteLimit::getScore)
                .collect(Collectors.toSet());
    }

    /**
     * Get the maximum limit of times the given player can vote with the given score.
     * <p>
     * Limit object returned by this method does not consider the votes which the player has already casted.
     * </p>
     * @param score target score
     * @param player target player
     * @return {@link Limit} object representing number of votes the player may cast.
     */
    public Limit getLimit(VoteScore score, Player player) {
        return limitSet.stream()
                .filter(voteLimit -> voteLimit.getScore().equals(score) && voteLimit.isApplicable(player))
                .map(VoteLimit::getLimit)
                .max(Limit::compareTo).orElse(Limit.zero);
    }

    /**
     * Get a map of {@link VoteScore} to {@link Limit}.
     * <p>
     * Returned map has scores which a given player can vote as keys,
     * and the limits of votes by the player with an associated score as values.
     * </p>
     * @param player target player
     * @return Map of {@link VoteScore} to {@link Limit}.
     */
    public Map<VoteScore, Limit> getLimitSet(Player player) {
        return this.getVotableScores()
                .stream()
                .collect(Collectors.toMap(Function.identity(), voteScore -> this.getLimit(voteScore, player)));
    }
}
