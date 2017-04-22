package com.github.kory33.signvote.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import com.github.kory33.signvote.constants.SubCommands;

public class RunCommandFilter extends AbstractFilter implements Filter {
    private static final String runCommand = SubCommands.ROOT + " " + SubCommands.RUN;
    private static final String commandLogRegex = "^\\w{3,16} issued server command: " + runCommand + " .*";
    private static final Pattern matchPattern = Pattern.compile(commandLogRegex);

    public RunCommandFilter() {
        super(Result.DENY, Result.NEUTRAL);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg,
                         final Object... params) {
        return filter(msg);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg,
                         final Throwable t) {
        if (msg == null) {
            return onMismatch;
        }
        return filter(msg.toString());
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg,
                         final Throwable t) {
        if (msg == null) {
            return onMismatch;
        }
        final String text = msg.getFormat();
        return filter(text);
    }

    @Override
    public Result filter(LogEvent event) {
        String text = event.getMessage().getFormat();
        return filter(text);
    }

    private Result filter(final String text) {
        if (text == null) {
            return super.onMismatch;
        }

        final Matcher matcher = matchPattern.matcher(text);
        if (matcher.find()) {
            return super.onMatch;
        }

        return super.onMismatch;
    }
}
