package com.github.kory33.signvote.constants

/**
 * Collection of nodes in plugin configuration file.
 * @author Kory
 */
object ConfigNodes {
    private val AUTOSAVE_ROOT = "autosave"
    val IS_AUTOSAVE_ENABLED = AUTOSAVE_ROOT + ".enabled"
    val AUTOSAVE_INTERVAL_TICKS = AUTOSAVE_ROOT + ".interval-ticks"
    val AUTOSAVE_SHOULD_LOG = AUTOSAVE_ROOT + ".enable-log"
    val VOTE_POINT_PROTECTION = "vote-point-protection"
}
