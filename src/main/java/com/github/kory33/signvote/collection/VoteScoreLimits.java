package com.github.kory33.signvote.collection;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class VoteScoreLimits {
    HashMap<Integer, HashMap<String, Integer>> limitMap;

    /**
     * Construct a VoteScoreLimits from an json data
     * @param jsonObject
     */
    public VoteScoreLimits(JsonObject jsonObject) {
        HashMap<Integer, HashMap<String, Integer>> limitMap = new HashMap<>();
        jsonObject.entrySet().stream().forEach(entry -> {
            try {
                int limit = Integer.parseInt(entry.getKey());
                HashMap<String, Integer> permLimits = new HashMap<>();
                entry.getValue().getAsJsonObject()
                    .entrySet()
                    .stream()
                    .forEach(permLimit -> permLimits.put(permLimit.getKey(), permLimit.getValue().getAsInt()));
                limitMap.put(limit, permLimits);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.limitMap = limitMap;
    }

    /**
     * Construct an empty VoteScoreLimits object.
     */
    public VoteScoreLimits() {
        this.limitMap = new HashMap<>();
    }

    /**
     * Get the Json representation of this object
     * @return
     */
    public JsonObject toJson() {
        return new Gson().toJsonTree(this.limitMap).getAsJsonObject();
    }

    public void addLimit(int score, String permission, int limit) {
        if (!this.limitMap.containsKey(score)) {
            this.limitMap.put(score, new HashMap<String, Integer>());
        }

        HashMap<String, Integer> permissiveLimits = this.limitMap.get(score);
        if(permissiveLimits.containsKey(permission)) {
            throw new IllegalArgumentException(
                    "Limit to the provided score (" + score +
                    ") and permission(" + permission + ") is already set!"
            );
        }

        permissiveLimits.put(permission, limit);
    }

    public void removeLimit(int score, String permission) {
        if (!this.limitMap.containsKey(score)) {
            return;
        }

        HashMap<String, Integer> permissiveLimits = this.limitMap.get(score);
        permissiveLimits.remove(permission);
    }

    public int getLimit(int score, Player player) {
        if (!this.limitMap.containsKey(score)) {
            return 0;
        }

        HashMap<String, Integer> permissiveLimits = this.limitMap.get(score);
        int maxLimit = 0;
        for (String permission: permissiveLimits.keySet()) {
            if (!player.hasPermission(permission)) {
                continue;
            }

            int limit = permissiveLimits.get(permission);
            maxLimit = Math.max(maxLimit, limit);
        }

        return maxLimit;
    }

    /**
     * Get all the possible votable scores given that the voter has full permissions
     * @return
     */
    public Set<Integer> getVotableScores() {
        return this.limitMap.keySet();
    }
}
