package com.github.kory33.signvote.utils.tellraw;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.constants.SubCommands;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class TellRawUtility {
    private final static String RUN_COMMAND = SubCommands.ROOT + " " + SubCommands.RUN;

    /**
     * Get a command associated with a given runnable.
     * @param runnableManager runnable hash table which is responsible for managing runnable objects
     * @param messageParts message component to which the runnable has to be bound
     * @param runnable a runnable object which gets invoked when a player clicks on the message component.
     * @return id of the bound runnable
     */
    public static long bindRunnableToMessageParts(RunnableHashTable runnableManager, MessageParts messageParts, Runnable runnable) {
        long runnableId = runnableManager.registerRunnable(runnable);
        String command = String.join(" ", RUN_COMMAND, String.valueOf(runnableId));

        messageParts.setClickEvent(ClickEventType.RUN_COMMAND, command);

        return runnableId;
    }
}
