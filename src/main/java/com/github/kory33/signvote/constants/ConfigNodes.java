package com.github.kory33.signvote.constants;

/**
 * Collection of nodes in plugin configuration file.
 * @author Kory
 */
public class ConfigNodes {
    private static final String AUTOSAVE_ROOT          = "autosave";
    public static final String IS_AUTOSAVE_ENABLED     = AUTOSAVE_ROOT + ".enabled";
    public static final String AUTOSAVE_INTERVAL_TICKS = AUTOSAVE_ROOT + ".interval-ticks";
    public static final String AUTOSAVE_SHOULD_LOG     = AUTOSAVE_ROOT + ".enable-log";
    public static final String VOTE_POINT_PROTECTION   = "vote-point-protection";
}
