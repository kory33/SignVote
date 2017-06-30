package com.github.kory33.signvote.manager;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import com.github.kory33.signvote.model.VoteLimit;
import com.google.gson.JsonArray;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that manages a collection of vote-limits
 */
public class VoteLimitManager {
    private final Set<VoteLimit> limitSet;

    public VoteLimitManager(JsonArray voteLimitsJsonArray) {
        this.limitSet = new HashSet<>();

        voteLimitsJsonArray.forEach(element -> {
            try {
                this.addVoteLimit(VoteLimit.fromJsonObject(element.getAsJsonObject()));
            } catch (InvalidLimitDataException e) {
                System.out.print("Ignoring json element : " + element.toString());
            }
        });
    }

    public void addVoteLimit(VoteLimit voteLimit) {
        this.limitSet.add(voteLimit);
    }

    public JsonArray toJsonArray() {
        JsonArray resultArray = new JsonArray();
        this.limitSet.forEach(limit -> resultArray.add(limit.toJsonObject()));
        
        return resultArray;
    }
}
