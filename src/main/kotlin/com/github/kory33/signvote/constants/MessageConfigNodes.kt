package com.github.kory33.signvote.constants

/**
 * Json nodes in message configuration.
 * Nodes that are preceded with "F_" are expected to be a format string.
 * @author kory
 */
object MessageConfigNodes {
    val MESSAGE_PREFIX = "prefix"

    private val COMMAND_ROOT = "command"
    val COMMAND_HELP_HEADER = COMMAND_ROOT + ".helpheader"

    val CREATE_COMMAND_HELP = COMMAND_ROOT + ".create.help"
    val F_SESSION_CREATED = COMMAND_ROOT + ".create.created"
    val SESSION_ALREADY_EXISTS = COMMAND_ROOT + ".create.error.sessionexists"

    val DELETE_SESS_COMMAND_HELP = COMMAND_ROOT + ".delete.help"
    val F_SESSION_DELETED = COMMAND_ROOT + ".delete.deleted"

    val LIST_COMMAND_HELP = COMMAND_ROOT + ".list.help"

    val DELETE_VP_COMMAND_HELP = COMMAND_ROOT + ".deletevp.help"
    val F_VOTEPOINT_DELETED = COMMAND_ROOT + ".deletevp.deleted"

    val ADD_SCORE_COMMAND_HELP = COMMAND_ROOT + ".addscore.help"
    val F_SCORE_LIMIT_ADDED = COMMAND_ROOT + ".addscore.added"

    val OPEN_COMMAND_HELP = COMMAND_ROOT + ".open.help"
    val F_SESSION_OPENED = COMMAND_ROOT + ".open.opened"
    val F_SESSION_ALREADY_OPENED = COMMAND_ROOT + ".open.error.alreadyopened"

    val CLOSE_COMMAND_HELP = COMMAND_ROOT + ".close.help"
    val F_SESSION_CLOSED = COMMAND_ROOT + ".close.closed"
    val F_SESSION_ALREADY_CLOSED = COMMAND_ROOT + ".close.error.alreadyclosed"

    val VOTE_COMMAND_HELP = COMMAND_ROOT + ".vote.help"
    val VOTED = COMMAND_ROOT + ".vote.voteaccepted"

    val INVALID_VOTE_SCORE = COMMAND_ROOT + ".vote.error.invalidscore"
    val REACHED_VOTE_SCORE_LIMIT = COMMAND_ROOT + ".vote.error.reachedlimit"
    val VOTEPOINT_ALREADY_VOTED = COMMAND_ROOT + ".vote.error.alreadyvoted"

    val UNVOTE_COMMAND_HELP = COMMAND_ROOT + ".unvote.help"
    val F_UNVOTED = COMMAND_ROOT + ".unvote.unvoted"
    val NOT_VOTED = COMMAND_ROOT + ".unvote.error.notvoted"

    val STATS_COMMAND_HELP = COMMAND_ROOT + ".stats.help"
    val STATS_MISSING_TYPE = COMMAND_ROOT + ".stats.missingtype"
    val STATS_INVALID_TYPE = COMMAND_ROOT + ".stats.invalidtype"

    val RELOAD_COMPLETE = COMMAND_ROOT + ".reload.complete"

    val SAVE_COMPLETE = COMMAND_ROOT + ".save.complete"


    private val GENERIC_COMMAND_ERROR = COMMAND_ROOT + ".generic.error"
    val COMMAND_ONLY_FOR_PLAYERS = GENERIC_COMMAND_ERROR + ".onlyforplayers"
    val SESSION_DOES_NOT_EXIST = GENERIC_COMMAND_ERROR + ".nosession"
    val VOTEPOINT_DOES_NOT_EXIST = GENERIC_COMMAND_ERROR + ".novotepoint"
    val MISSING_PERMS = GENERIC_COMMAND_ERROR + ".missingpermission"
    val VOTEPOINT_NAME_INVALID = GENERIC_COMMAND_ERROR + ".vpinvalidname"
    val VOTEPOINT_ALREADY_EXISTS = GENERIC_COMMAND_ERROR + ".vpexists"
    val INVALID_NUMBER = GENERIC_COMMAND_ERROR + ".invalidnumber"

    private val VOTEPOINT_MESSAGE_ROOT = "votepoint"
    val VOTEPOINT_CREATED = VOTEPOINT_MESSAGE_ROOT + ".created"
    val F_VOTEPOINT_BREAK = VOTEPOINT_MESSAGE_ROOT + ".break"

    private val UI_ROOT = "ui"

    val UI_HEADER = UI_ROOT + ".header"
    val UI_FOOTER = UI_ROOT + ".footer"
    val UI_BUTTON = UI_ROOT + ".button"
    val UI_CANCEL = UI_ROOT + ".cancel"
    val UI_CANCELLED = UI_ROOT + ".cancelled"

    val UI_ACTIVE_BUTTON_COLOR = UI_ROOT + ".activeButtonColor"
    val UI_INACTIVE_BUTTON_COLOR = UI_ROOT + ".inactiveButtonColor"
    val UI_PREV_BUTTON = UI_ROOT + ".prevbutton"
    val UI_NEXT_BUTTON = UI_ROOT + ".nextbutton"
    val F_UI_PAGE_DISPLAY = UI_ROOT + ".pagedisp"

    private val VOTE_UI_ROOT = UI_ROOT + ".vote"
    val VOTE_UI_HEADING = VOTE_UI_ROOT + ".heading"
    val VOTE_UI_SCORE_SELECTION = VOTE_UI_ROOT + ".scoreselection"
    val VOTE_UI_NONE_AVAILABLE = VOTE_UI_ROOT + ".noavailablevotes"

    private val UNVOTE_UI = UI_ROOT + ".unvote"
    val UNVOTE_UI_HEADING = UNVOTE_UI + ".heading"
    val UNVOTE_UI_COMFIRM = UNVOTE_UI + ".comfirm"

    private val LIST_UI = UI_ROOT + ".list"
    val LIST_UI_HEADING = LIST_UI + ".heading"
    val F_LIST_UI_ENTRY_TEMPLATE = LIST_UI + ".entrytemplate"
    val LIST_UI_SESSION_OPEN = LIST_UI + ".state.open"
    val LIST_UI_SESSION_CLOSED = LIST_UI + ".state.closed"

    private val ADDSCORE_UI = UI_ROOT + ".addscore"
    val ADDSCORE_UI_HEADING = ADDSCORE_UI + ".heading"
    val ADDSCORE_UI_SCORE = ADDSCORE_UI + ".score"
    val ADDSCORE_UI_LIMIT = ADDSCORE_UI + ".limit"
    val ADDSCORE_UI_PERMISSION = ADDSCORE_UI + ".permission"
    val ADDSCORE_UI_SCORE_NOT_SET = ADDSCORE_UI + ".scorenotset"
    val ADDSCORE_UI_SUBMIT = ADDSCORE_UI + ".submit"

    private val STATS_UI = UI_ROOT + ".stats"
    val F_STATS_UI_HEADING = STATS_UI + ".heading"
    val F_STATS_ENTRY_TEMPLATE = STATS_UI + ".template"

    private val UI_FORM_ROOT = UI_ROOT + ".form"
    val F_UI_FORM_LABEL = UI_FORM_ROOT + ".label"
    val F_UI_FORM_VALUE = UI_FORM_ROOT + ".value"
    val UI_FORM_NOTSET = UI_FORM_ROOT + ".notset"
    val UI_FORM_EDIT_BUTTON = UI_FORM_ROOT + ".editbutton"
    val F_UI_FORM_PROMPT = UI_FORM_ROOT + ".prompt"
    val UI_FORM_INVALID_INPUT = UI_FORM_ROOT + ".invalidinput"
    val UI_CANCEL_INPUT_BUTTON = UI_FORM_ROOT + ".inputcancelbutton"
    val UI_INPUT_CANCELLED = UI_FORM_ROOT + ".inputcancelled"

    private val GENERIC = "generic"
    val INFINITE = GENERIC + ".infinite"
    val VOTE_SESSION_CLOSED = GENERIC + ".vsclosed"

    /**
     * Stats type constants should be referred only indirectly from StatsType enum class.
     */
    internal val STATS_TYPE_VOTES = GENERIC + ".votes"
    internal val STATS_TYPE_SCORE = GENERIC + ".score"
    internal val STATS_TYPE_MEAN = GENERIC + ".mean"
}
