package com.github.kory33.signvote.constants;

import lombok.Getter;

/**
 * Enumeration of statistics modes.
 * @author Kory
 */
public enum StatsType {
    VOTES("votes", MessageConfigNodes.STATS_TYPE_VOTES),
    SCORE("score", MessageConfigNodes.STATS_TYPE_SCORE),
    MEAN("mean", MessageConfigNodes.STATS_TYPE_MEAN);

    @Getter private final String type;
    @Getter private final String typeMessageNode;

    StatsType(String type, String messageNode) {
        this.type = type;
        this.typeMessageNode = messageNode;
    }

    @Override
    public String toString() {
        return this.getType();
    }

    /**
     * Get the enum value corresponding to the given type
     * @param typeString type of the statistics
     * @return enum value corresponding to the given type string
     */
    public static StatsType fromString(String typeString) {
        for(StatsType type: StatsType.values()) {
            if (type.getType().equalsIgnoreCase(typeString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No stats type \'" + typeString + "\' is available.");
    }
}
