package com.github.kory33.signvote.constants;

public class MessageConfigurationNodes {
    public static final String COMMAND_ROOT = "command";
    public static final String COMMAND_HELP = COMMAND_ROOT + ".help";
    
    public static final String CREATE_COMMAND_HELP      = COMMAND_ROOT + ".create.help";
    public static final String ADD_SCORE_COMMAND_HELP   = COMMAND_ROOT + ".addscore.help";
    
    public static final String OPEN_COMMAND_HELP        = COMMAND_ROOT + ".open.help";
    public static final String SESSION_ALREADY_OPENED   = COMMAND_ROOT + ".open.error.alreadyopened";
    
    public static final String CLOSE_COMMAND_HELP       = COMMAND_ROOT + ".close.help";
    public static final String SESSION_ALREADY_CLOSED   = COMMAND_ROOT + ".close.error.alreadyclosed";
    
    public static final String VOTE_COMMAND_HELP        = COMMAND_ROOT + ".vote.help";
    public static final String VOTED                    = COMMAND_ROOT + ".vote.voteaccepted";
    
    public static final String INVALID_VOTE_SCORE       = COMMAND_ROOT + ".vote.error.invalidscore";
    public static final String REACHED_VOTE_SCORE_LIMIT = COMMAND_ROOT + ".vote.error.reachedlimit";
    public static final String VOTEPOINT_ALREADY_VOTED  = COMMAND_ROOT + ".vote.error.alreadyvoted";
    
    public static final String ERROR_ROOT = "error";
    
    public static final String MISSING_PERMS = ERROR_ROOT + ".missingpermission";
    
    public static final String SESSION_DOES_NOT_EXIST   = ERROR_ROOT + ".nosession";
    public static final String VOTEPOINT_NAME_EMPTY     = ERROR_ROOT + ".vpemptyname";
    public static final String VOTEPOINT_ALREADY_EXISTS = ERROR_ROOT + ".vpexists";
    public static final String VOTEPOINT_DOES_NOT_EXIST = ERROR_ROOT + ".novotepoint";
    public static final String COMMAND_ONLY_FOR_PLAYERS = ERROR_ROOT + ".onlyforplayers";
}
