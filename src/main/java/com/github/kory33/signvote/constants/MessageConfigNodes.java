package com.github.kory33.signvote.constants;

/**
 * Json nodes in message configuration.
 * Nodes that are preceded with "F_" are expected to be a format string.
 * @author kory
 *
 */
public class MessageConfigNodes {
    public static final String MESSAGE_PREFIX = "prefix";

    public static final String COMMAND_ROOT = "command";
    public static final String COMMAND_HELP_HEADER = COMMAND_ROOT + ".helpheader";

    public static final String CREATE_COMMAND_HELP      = COMMAND_ROOT + ".create.help";
    public static final String F_SESSION_CREATED        = COMMAND_ROOT + ".create.created";
    public static final String SESSION_ALREADY_EXISTS   = COMMAND_ROOT + ".create.error.sessionexists";

    public static final String DELETE_SESS_COMMAND_HELP = COMMAND_ROOT + ".delete.help";
    public static final String F_SESSION_DELETED        = COMMAND_ROOT + ".delete.deleted";

    public static final String LIST_COMMAND_HELP        = COMMAND_ROOT + ".list.help";

    public static final String DELETE_VP_COMMAND_HELP   = COMMAND_ROOT + ".deletevp.help";
    public static final String F_VOTEPOINT_DELETED      = COMMAND_ROOT + ".deletevp.deleted";

    public static final String ADD_SCORE_COMMAND_HELP   = COMMAND_ROOT + ".addscore.help";
    public static final String F_SCORE_LIMIT_ADDED      = COMMAND_ROOT + ".addscore.added";

    public static final String OPEN_COMMAND_HELP        = COMMAND_ROOT + ".open.help";
    public static final String F_SESSION_OPENED         = COMMAND_ROOT + ".open.opened";
    public static final String F_SESSION_ALREADY_OPENED = COMMAND_ROOT + ".open.error.alreadyopened";

    public static final String CLOSE_COMMAND_HELP       = COMMAND_ROOT + ".close.help";
    public static final String F_SESSION_CLOSED         = COMMAND_ROOT + ".close.closed";
    public static final String F_SESSION_ALREADY_CLOSED = COMMAND_ROOT + ".close.error.alreadyclosed";

    public static final String VOTE_COMMAND_HELP        = COMMAND_ROOT + ".vote.help";
    public static final String VOTED                    = COMMAND_ROOT + ".vote.voteaccepted";

    public static final String INVALID_VOTE_SCORE       = COMMAND_ROOT + ".vote.error.invalidscore";
    public static final String REACHED_VOTE_SCORE_LIMIT = COMMAND_ROOT + ".vote.error.reachedlimit";
    public static final String VOTEPOINT_ALREADY_VOTED  = COMMAND_ROOT + ".vote.error.alreadyvoted";

    public static final String UNVOTE_COMMAND_HELP      = COMMAND_ROOT + ".unvote.help";
    public static final String F_UNVOTED                = COMMAND_ROOT + ".unvote.unvoted";
    public static final String NOT_VOTED                = COMMAND_ROOT + ".unvote.error.notvoted";

    public static final String STATS_COMMAND_HELP       = COMMAND_ROOT + ".stats.help";
    public static final String STATS_MISSING_TYPE       = COMMAND_ROOT + ".stats.missingtype";
    public static final String STATS_INVALID_TYPE       = COMMAND_ROOT + ".stats.invalidtype";

    public static final String RELOAD_COMPLETE          = COMMAND_ROOT + ".reload.complete";

    public static final String SAVE_COMPLETE            = COMMAND_ROOT + ".save.complete";


    public static final String GENERIC_COMMAND_ERROR    = COMMAND_ROOT + ".generic.error";
    public static final String COMMAND_ONLY_FOR_PLAYERS = GENERIC_COMMAND_ERROR + ".onlyforplayers";
    public static final String SESSION_DOES_NOT_EXIST   = GENERIC_COMMAND_ERROR + ".nosession";
    public static final String VOTEPOINT_DOES_NOT_EXIST = GENERIC_COMMAND_ERROR + ".novotepoint";
    public static final String MISSING_PERMS            = GENERIC_COMMAND_ERROR + ".missingpermission";
    public static final String VOTEPOINT_NAME_INVALID   = GENERIC_COMMAND_ERROR + ".vpinvalidname";
    public static final String VOTEPOINT_ALREADY_EXISTS = GENERIC_COMMAND_ERROR + ".vpexists";
    public static final String INVALID_NUMBER           = GENERIC_COMMAND_ERROR + ".invalidnumber";

    public static final String VOTEPOINT_MESSAGE_ROOT   = "votepoint";
    public static final String VOTEPOINT_CREATED        = VOTEPOINT_MESSAGE_ROOT + ".created";
    public static final String F_VOTEPOINT_BREAK        = VOTEPOINT_MESSAGE_ROOT + ".break";

    public static final String UI_ROOT                  = "ui";

    public static final String UI_HEADER                = UI_ROOT + ".header";
    public static final String UI_FOOTER                = UI_ROOT + ".footer";
    public static final String UI_BUTTON                = UI_ROOT + ".button";
    public static final String UI_CANCEL                = UI_ROOT + ".cancel";
    public static final String UI_CANCELLED             = UI_ROOT + ".cancelled";

    public static final String UI_PREV_BUTTON           = UI_ROOT + ".prevbutton";
    public static final String UI_NEXT_BUTTON           = UI_ROOT + ".nextbutton";
    public static final String UI_PREV_BUTTON_INACTIVE  = UI_ROOT + ".inactiveprevbutton";
    public static final String UI_NEXT_BUTTON_INACTIVE  = UI_ROOT + ".inactivenextbutton";
    public static final String F_UI_PAGE_DISPLAY        = UI_ROOT + ".pagedisp";

    public static final String VOTE_UI_ROOT             = UI_ROOT + ".vote";
    public static final String VOTE_UI_HEADING          = VOTE_UI_ROOT + ".heading";
    public static final String VOTE_UI_SCORE_SELECTION  = VOTE_UI_ROOT + ".scoreselection";
    public static final String VOTE_UI_NONE_AVAILABLE   = VOTE_UI_ROOT + ".noavailablevotes";

    public static final String UNVOTE_UI                = UI_ROOT + ".unvote";
    public static final String UNVOTE_UI_HEADING        = UNVOTE_UI + ".heading";
    public static final String UNVOTE_UI_COMFIRM        = UNVOTE_UI + ".comfirm";

    public static final String LIST_UI                  = UI_ROOT + ".list";
    public static final String LIST_UI_HEADING          = LIST_UI + ".heading";
    public static final String F_LIST_UI_ENTRY_TEMPLATE = LIST_UI + ".entrytemplate";
    public static final String LIST_UI_SESSION_OPEN     = LIST_UI + ".state.open";
    public static final String LIST_UI_SESSION_CLOSED   = LIST_UI + ".state.closed";

    public static final String ADDSCORE_UI              = UI_ROOT + ".addscore";
    public static final String ADDSCORE_UI_HEADING      = ADDSCORE_UI + ".heading";
    public static final String ADDSCORE_UI_SCORE        = ADDSCORE_UI + ".score";
    public static final String ADDSCORE_UI_LIMIT        = ADDSCORE_UI + ".limit";
    public static final String ADDSCORE_UI_PERMISSION   = ADDSCORE_UI + ".permission";
    public static final String ADDSCORE_UI_SCORE_NOT_SET= ADDSCORE_UI + ".scorenotset";
    public static final String ADDSCORE_UI_SUBMIT       = ADDSCORE_UI + ".submit";

    public static final String STATS_UI                 = UI_ROOT + ".stats";
    public static final String F_STATS_UI_HEADING       = STATS_UI + ".heading";
    public static final String F_STATS_ENTRY_TEMPLATE   = STATS_UI + ".template";

    public static final String UI_FORM_ROOT             = UI_ROOT + ".form";
    public static final String F_UI_FORM_NAME           = UI_FORM_ROOT + ".name";
    public static final String F_UI_FORM_VALUE          = UI_FORM_ROOT + ".value";
    public static final String UI_FORM_NOTSET           = UI_FORM_ROOT + ".notset";
    public static final String UI_FORM_EDIT_BUTTON      = UI_FORM_ROOT + ".editbutton";
    public static final String F_UI_FORM_PROMPT         = UI_FORM_ROOT + ".prompt";
    public static final String UI_FORM_INVALID_INPUT    = UI_FORM_ROOT + ".invalidinput";
    public static final String UI_CANCEL_INPUT_BUTTON   = UI_FORM_ROOT + ".inputcancelbutton";
    public static final String UI_INPUT_CANCELLED       = UI_FORM_ROOT + ".inputcancelled";

    public static final String GENERIC                  = "generic";
    public static final String INFINITE                 = GENERIC + ".infinite";

    public static final String STATS_TYPE_VOTES         = GENERIC + ".votes";
    public static final String STATS_TYPE_SCORE         = GENERIC + ".score";
    public static final String STATS_TYPE_MEAN          = GENERIC + ".mean";
    public static final String VOTE_SESSION_CLOSED      = GENERIC + ".vsclosed";
}
