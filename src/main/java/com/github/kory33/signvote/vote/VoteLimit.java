package com.github.kory33.signvote.vote;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.entity.Player;

/**
 * A class that represents a limit of vote counts
 */
@Value
@RequiredArgsConstructor
public class VoteLimit {
    /** Json keys */
    private static final String JSON_SCORE_KEY = "score";
    private static final String JSON_LIMIT_KEY = "limit";
    private static final String JSON_PERMS_KEY = "permission";

    private final VoteScore score;
    private final Limit limit;

    @Getter(AccessLevel.NONE)
    private final String permission;

    public VoteLimit(VoteScore score, Limit limit) {
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

    /**
     * Construct a VoteLimit object from a json object
     * @param jsonObject object to be converted to a VoteLimit
     * @return converted object
     * @throws InvalidLimitDataException when the given jsonObject is invalid
     */
    public static VoteLimit fromJsonObject(JsonObject jsonObject) throws InvalidLimitDataException {
        JsonElement scoreElement = jsonObject.get(JSON_SCORE_KEY);
        JsonElement limitElement = jsonObject.get(JSON_LIMIT_KEY);
        JsonElement permissionElement = jsonObject.get(JSON_PERMS_KEY);

        VoteScore score = new VoteScore(scoreElement.getAsInt());
        Limit limit = Limit.fromString(limitElement.getAsString());

        if (permissionElement.isJsonNull()) {
            return new VoteLimit(score, limit);
        }

        String permissionString = permissionElement.getAsString();
        return new VoteLimit(score, limit, permissionString);
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(JSON_SCORE_KEY, this.score.toInt());
        jsonObject.addProperty(JSON_LIMIT_KEY, this.limit.toString());
        jsonObject.addProperty(JSON_PERMS_KEY, this.permission);

        return jsonObject;
    }
}
