package com.github.kory33.signvote.constants

/**
 * Enumeration of statistics modes.
 * @author Kory
 */
enum class StatsType(val type: String, val typeMessageNode: String) {
    VOTES("votes", MessageConfigNodes.STATS_TYPE_VOTES),
    SCORE("score", MessageConfigNodes.STATS_TYPE_SCORE),
    MEAN("mean", MessageConfigNodes.STATS_TYPE_MEAN);

    override fun toString(): String {
        return this.type
    }

    companion object {

        /**
         * Get the enum value corresponding to the given type
         * @param typeString type of the statistics
         * *
         * @return enum value corresponding to the given type string
         */
        fun fromString(typeString: String): StatsType {
            StatsType.values()
                    .filter { it.type.equals(typeString, ignoreCase = true) }
                    .forEach { return it }
            throw IllegalArgumentException("No stats type \'$typeString\' is available.")
        }
    }
}
