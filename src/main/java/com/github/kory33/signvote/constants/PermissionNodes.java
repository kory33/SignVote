package com.github.kory33.signvote.constants;

public class PermissionNodes {
    private static final String BASE_NODE = "signvote";
    
    public static final String VOTE           = BASE_NODE + ".vote";
    public static final String CREATE_SIGN    = BASE_NODE + ".createsign";
    public static final String CREATE_SESSION = BASE_NODE + ".createsession";
    public static final String MODIFY_SESSION = BASE_NODE + ".modifysession";

    public static final String OPEN_SESSION   = BASE_NODE + ".opensession";
    public static final String CLOSE_SESSION  = BASE_NODE + ".closesession";

    public static final String DELETE_VOTEPOINT = BASE_NODE + ".deletevotepoint";

    public static final String DELETE_SESSION = BASE_NODE + ".deletesession";

    public static final String RELOAD         = BASE_NODE + ".reload";
}
