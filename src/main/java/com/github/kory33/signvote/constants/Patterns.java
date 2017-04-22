package com.github.kory33.signvote.constants;

import java.util.regex.Pattern;

public class Patterns {
    public final static Pattern JSON_FILE_NAME = Pattern.compile("^(.*)"+ Formats.JSON_EXT +"$");
}
