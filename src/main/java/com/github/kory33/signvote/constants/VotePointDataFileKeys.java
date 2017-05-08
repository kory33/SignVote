package com.github.kory33.signvote.constants;

/**
 * Keys in json file which stores information about vote points
 * @author Kory
 */
public class VotePointDataFileKeys {
    public static final String NAME = "name";

    private static final String VOTE_SIGN      = "votesign";
    public static final String VOTE_SIGN_WORLD = VOTE_SIGN + ".world";
    private static final String VOTE_SIGN_LOC  = VOTE_SIGN + ".location";
    public static final String VOTE_SIGN_LOC_X = VOTE_SIGN_LOC + ".X";
    public static final String VOTE_SIGN_LOC_Y = VOTE_SIGN_LOC + ".Y";
    public static final String VOTE_SIGN_LOC_Z = VOTE_SIGN_LOC + ".Z";
}
