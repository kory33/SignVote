package com.github.kory33.signvote.constants;

/**
 * Json nodes in message configuration.
 * Nodes that are preceded with "F_" are expected to be a format string.
 * @author kory
 *
 */
public class MessageConfigurationNodes {
    public static final String MESSAGE_PREFIX = "prefix";

    public static final String COMMAND_ROOT = "command";
    public static final String COMMAND_HELP_HEADER = COMMAND_ROOT + ".helpheader";

    public static final String CREATE_COMMAND_HELP      = COMMAND_ROOT + ".create.help";
    public static final String F_SESSION_CREATED        = COMMAND_ROOT + ".create.created";
    public static final String SESSION_ALREADY_EXISTS   = COMMAND_ROOT + ".create.error.sessionexists";

    public static final String DELETE_SESS_COMMAND_HELP = COMMAND_ROOT + ".delete.help";
    public static final String F_SESSION_DELETED        = COMMAND_ROOT + ".delete.deleted";

    public static final String DELETE_VP_COMMAND_HELP   = COMMAND_ROOT + ".deletevp.help";
    public static final String F_VOTEPOINT_DELETED      = COMMAND_ROOT + ".deletevp.deleted";

    public static final String ADD_SCORE_COMMAND_HELP   = COMMAND_ROOT + ".addscore.help";

    public static final String OPEN_COMMAND_HELP        = COMMAND_ROOT + ".open.help";
    public static final String F_SESSION_OPENED         = COMMAND_ROOT + ".open.opened";
    public static final String F_SESSION_ALREADY_OPENED = COMMAND_ROOT + ".open.error.alreadyopened";

    public static final String CLOSE_COMMAND_HELP       = COMMAND_ROOT + ".close.help";
    public static final String F_SESSION_CLOSED         = COMMAND_ROOT + ".close.closed";
    public static final String F_SESSION_ALREADY_CLOSED = COMMAND_ROOT + ".close.error.alreadyclosed";

    public static final String VOTE_COMMAND_HELP        = COMMAND_ROOT + ".vote.help";
    public static final String VOTED                    = COMMAND_ROOT + ".vote.voteaccepted";
    public static final String F_NOT_VOTED              = COMMAND_ROOT + ".vote.notvoted";

    public static final String INVALID_VOTE_SCORE       = COMMAND_ROOT + ".vote.error.invalidscore";
    public static final String REACHED_VOTE_SCORE_LIMIT = COMMAND_ROOT + ".vote.error.reachedlimit";
    public static final String VOTEPOINT_ALREADY_VOTED  = COMMAND_ROOT + ".vote.error.alreadyvoted";

    public static final String UNVOTE_COMMAND_HELP      = COMMAND_ROOT + ".unvote.help";
    public static final String F_UNVOTED                = COMMAND_ROOT + ".unvote.unvoted";

    public static final String RELOAD_COMPLETE          = COMMAND_ROOT + ".reload.complete";

    public static final String SAVE_COMPLETE            = COMMAND_ROOT + ".save.complete";


    public static final String GENERIC_COMMAND_ERROR    = COMMAND_ROOT + ".generic.error";
    public static final String COMMAND_ONLY_FOR_PLAYERS = GENERIC_COMMAND_ERROR + ".onlyforplayers";
    public static final String SESSION_DOES_NOT_EXIST   = GENERIC_COMMAND_ERROR + ".nosession";
    public static final String VOTEPOINT_DOES_NOT_EXIST = GENERIC_COMMAND_ERROR + ".novotepoint";
    public static final String MISSING_PERMS            = GENERIC_COMMAND_ERROR + ".missingpermission";
    public static final String VOTEPOINT_NAME_EMPTY     = GENERIC_COMMAND_ERROR + ".vpemptyname";
    public static final String VOTEPOINT_ALREADY_EXISTS = GENERIC_COMMAND_ERROR + ".vpexists";

    public static final String VOTEPOINT_MESSAGE_ROOT   = "votepoint";
    public static final String VOTEPOINT_CREATED        = VOTEPOINT_MESSAGE_ROOT + ".created";
    public static final String F_VOTEPOINT_BREAK        = VOTEPOINT_MESSAGE_ROOT + ".break";

    public static final String UI_ROOT                  = "ui";

    public static final String UI_HEADER                = UI_ROOT + ".header";
    public static final String UI_FOOTER                = UI_ROOT + ".footer";
    public static final String UI_BUTTON                = UI_ROOT + ".button";
    public static final String UI_CANCEL                = UI_ROOT + ".cancel";
    public static final String UI_CANCELLED             = UI_ROOT + ".cancelled";

    public static final String VOTE_UI_ROOT             = UI_ROOT + ".vote";
    public static final String VOTE_UI_HEADING          = VOTE_UI_ROOT + ".heading";
    public static final String VOTE_UI_SCORE_SELECTION  = VOTE_UI_ROOT + ".scoreselection";
    public static final String VOTE_UI_NONE_AVAILABLE   = VOTE_UI_ROOT + ".noavailablevotes";

    public static final String UNVOTE_UI                = UI_ROOT + ".unvote";
    public static final String UNVOTE_UI_HEADING        = UNVOTE_UI + ".heading";
    public static final String UNVOTE_UI_COMFIRM        = UNVOTE_UI + ".comfirm";
}
