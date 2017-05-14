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

/**
 * This class acts as a "filter" for runnable invocation commands.
 *
 * When a player clicks on a button in {@link com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface},
 * a command `/signvote:signvote run` is run in order to invoke a registered runnable.
 *
 * However, this can cause a lot of spam in the console. This class is useful in preventing such logs.
 */
public class CommandFilter extends AbstractFilter implements Filter {
    private final Pattern commandMatchPattern;
    public CommandFilter(String command) {
        super(Result.DENY, Result.NEUTRAL);
        String commandLogRegex = "^\\w{3,16} issued server command: " + command + ".*";
        this.commandMatchPattern = Pattern.compile(commandLogRegex);

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

        final Matcher matcher = this.commandMatchPattern.matcher(text);
        if (matcher.find()) {
            return super.onMatch;
        }

        return super.onMismatch;
    }
}
