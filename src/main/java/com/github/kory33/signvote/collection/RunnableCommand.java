package com.github.kory33.signvote.collection;

import lombok.Value;

/**
 * @author Kory
 */

@Value
public class RunnableCommand {
    String commandString;
    long runnableId;
}
