package com.github.kory33.messaging.tellraw;

import java.util.ArrayList;
import java.util.Collection;

import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * A class representing an ArrayList of MessageParts
 * @author kory
 */
public class MessagePartsList extends ArrayList<MessageParts> {
    private static final long serialVersionUID = -3095004887890313967L;

    /**
     * Append a MessageParts and a line break at the end of this list.
     * @param messageParts
     * @return true (As specified by {@link #Collection.add(E)})
     */
    public boolean addLine(MessageParts messageParts) {
        this.add(messageParts);
        this.add(new MessageParts("\n"));
        return true;
    }

    /**
     * Append a MessageParts and a line break at the end of this list.
     * @param messageParts
     * @return true (As specified by {@link #Collection.add(E)})
     */
    public boolean addLine(String messageString) {
        this.addLine(new MessageParts(messageString));
        return true;
    }

    /**
     * Insert a MessageParts and a line break at the position specified by the index.
     * @param messageParts
     * @param index
     */
    public void addLine(int index, MessageParts messageParts) {
        this.add(index, new MessageParts("\n"));
        this.add(index, messageParts);
    }

    /**
     * Insert an array of MessageParts and a line break at the position specified by the index.
     * @param messageParts
     * @param index
     */
    public void addLine(int index, Collection<? extends MessageParts> c) {
        this.add(index, new MessageParts("\n"));
        this.addAll(index, c);
    }

    /**
     * Append an array of MessageParts and a line break at the end of the collection
     * @param messageParts
     * @param index
     */
    public void addLine(Collection<? extends MessageParts> c) {
        this.addAll(c);
        this.addLine("");
    }
}
