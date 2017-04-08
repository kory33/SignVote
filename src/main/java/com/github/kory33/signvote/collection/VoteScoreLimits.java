package com.github.kory33.signvote.collection;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.json.JSONObject;

public class VoteScoreLimits {
    HashMap<Integer, HashMap<String, Integer>> limitMap;
    
    public VoteScoreLimits(JSONObject jsonObject) {
        // TODO implementations
    }
    
    /**
     * Construct an empty VoteScoreLimits object.
     */
    public VoteScoreLimits() {
        this.limitMap = new HashMap<>();
    }
    
    public JSONObject toJson() {
        // TODO implementations
        return null;
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
}
