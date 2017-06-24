package com.github.kory33.signvote.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * A class that represents a limit of vote counts
 */
@Value
@RequiredArgsConstructor
public class VoteLimit {
    private final int score;
    private final Limit limit;
    private final Permission permission;

    public VoteLimit(int score, Limit limit) {
        this(score, limit, null);
    }

    /**
     * Returns if the limit applies to the given player
     * @param player player to be inspected
     * @return true if the player has permissions associated to this vote-limit
     */
    public boolean isApplicable(Player player) {
        return this.permission == null || player.hasPermission(this.permission);
    }
}
