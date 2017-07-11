package com.github.kory33.signvote.constants

import java.util.regex.Pattern

/**
 * Collection of regular expression patterns used by SignVote plugin
 * @author Kory
 */
object Patterns {
    val JSON_FILE_NAME: Pattern = Pattern.compile("^(.*)" + Formats.JSON_EXT + "$")
    val PATTERN_VALID_VP_NAME: Pattern = Pattern.compile("^(\\w|(\\w[^\\\\/]*\\w))$")
}
