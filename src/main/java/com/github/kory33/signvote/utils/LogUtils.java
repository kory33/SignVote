package com.github.kory33.signvote.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.RegexFilter;

import com.github.kory33.signvote.constants.SubCommands;

public class LogUtils {
    private static Filter registeredRunCommandFilter;

    public static void addRunCommandFilter() {
        if (registeredRunCommandFilter != null) {
            return;
        }

        String runCommand = SubCommands.ROOT + " " + SubCommands.RUN;
        String commandLogRegex = "^[.*]: \\w{3,16} issued server command: " + runCommand;

        try {
            Filter runCommandFilter = RegexFilter.createFilter(commandLogRegex, null, true, Result.DENY, Result.NEUTRAL);
            ((Logger) LogManager.getRootLogger()).addFilter(runCommandFilter);
            registeredRunCommandFilter = runCommandFilter;
        } catch (IllegalArgumentException | IllegalAccessException e) {}
    }
}
