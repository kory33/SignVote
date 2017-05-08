package com.github.kory33.signvote.constants;

import java.util.regex.Pattern;

/**
 * Collection of regular expression patterns used by SignVote plugin
 * @author Kory
 */
public class Patterns {
    public final static Pattern JSON_FILE_NAME = Pattern.compile("^(.*)"+ Formats.JSON_EXT +"$");
    public static final Pattern PATTERN_VALID_VP_NAME = Pattern.compile("^(\\w|(\\w[^\\\\/]*\\w))$");
}
