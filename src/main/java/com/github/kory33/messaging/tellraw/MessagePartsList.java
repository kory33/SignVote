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

    public MessagePartsList() {
        super();
    }

    /**
     * Construct list containing a given message part.
     * @param messageParts a message part to be contained in the list
     */
    public MessagePartsList(MessageParts messageParts) {
        super();
        this.add(messageParts);
    }

    /**
     * Construct list containing a message part which has given message string as a body.
     * @param messageString a string to be contained in the list
     */
    public MessagePartsList(String messageString) {
        super();
        this.add(messageString);
    }

    /**
     * Append a MessageParts and a line break at the end of this list.
     * @param messageParts a message to be added along with a line break
     */
    public void addLine(MessageParts messageParts) {
        this.add(messageParts);
        this.add("\n");
    }

    /**
     * Append a MessageParts and a line break at the end of this list.
     * @param messageString a string to be added along with a line ending
     */
    public void addLine(String messageString) {
        this.addLine(new MessageParts(messageString));
    }

    /**
     * Append an array of MessageParts and a line break at the end of the collection
     * @param collection a collection of messages to be inserted into the list
     */
    public void addLine(Collection<? extends MessageParts> collection) {
        this.addAll(collection);
        this.addLine("");
    }

    /**
     * Append a string element at the end of this list
     * @param message a message to be added
     */
    public void add(String message) {
        this.add(new MessageParts(message));
    }
}
