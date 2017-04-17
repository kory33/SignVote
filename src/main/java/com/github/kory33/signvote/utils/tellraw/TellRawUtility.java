package com.github.kory33.signvote.utils.tellraw;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class TellRawUtility {
    private final static String RUN_COMMAND = "/signvote run";
    public static void bindRunnableToMessageParts(RunnableHashTable runnableManager, MessageParts messageParts, Runnable runnable) {
        long runnableId = runnableManager.registerRunnable(runnable);
        String command = String.join(" ", RUN_COMMAND, String.valueOf(runnableId));

        messageParts.setClickEvent(ClickEventType.RUN_COMMAND, command);
    }
}
