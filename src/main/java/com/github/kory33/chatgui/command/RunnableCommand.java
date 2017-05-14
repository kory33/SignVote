package com.github.kory33.chatgui.command;

import lombok.Value;

/**
 * @author Kory
 */

@Value
public class RunnableCommand {
    String commandString;
    long runnableId;
}
