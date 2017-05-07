package com.github.kory33.signvote.ui.player.model;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

import lombok.Getter;

/**
 * A class representing an interface whose buttons at the bottom allow the player to move between pages.
 * @author kory
 *
 */
public abstract class BrowseablePageInterface extends PlayerClickableChatInterface {
    /**
     * Number of entry(row) per page.
     * This value should be used to calculate the index rather than hard-coding it.
     */
    protected static final int ENTRY_PER_PAGE = 10;

    @Getter private int requestedPageIndex;
    protected final PlayerInteractiveInterfaceManager interfaceManager;

    /**
     * Get the list of all entries.
     * The list must be ordered in an appropriate way.
     * Each entry may not contain line ending at the end.
     * @return
     */
    protected abstract ArrayList<MessagePartsList> getEntryList();

    /**
     * Create new instance of browseable chat interface with the specified index.
     */
    protected abstract BrowseablePageInterface yieldPage(int pageIndex);

    /**
     * Get the line which is inserted before the table's main body
     * @return
     */
    protected abstract MessagePartsList getHeading();

    public BrowseablePageInterface(Player player, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, messageConfiguration, runnableHashTable);
        this.interfaceManager = interfaceManager;
        this.requestedPageIndex = pageIndex;
    }

    /**
     * Get the copy of the new interface with another index specified.
     * No registration to the interface manager is done in this constructor.
     * @param oldInterface
     * @param newIndex
     */
    public BrowseablePageInterface(BrowseablePageInterface oldInterface, int newIndex) {
        this(oldInterface.getTargetPlayer(), oldInterface.messageConfig, oldInterface.getRunnableHashTable(),
                oldInterface.interfaceManager, newIndex);
    }

    /**
     * Get the body of the browseable table.
     * @param finalPageIndex processed page index number(should not be out of range)
     * @return
     */
    private MessagePartsList getBrowseButtonLine(final int finalPageIndex, final int maximumPageIndex) {
        MessagePartsList messagePartsList = new MessagePartsList();

        MessageParts prevButton = (finalPageIndex != 0) ?
                this.getButton(() -> {
                    BrowseablePageInterface newInterface = this.yieldPage(finalPageIndex - 1);
                    this.interfaceManager.registerInterface(newInterface);
                    newInterface.send();
                }, this.getFormattedMessagePart(MessageConfigNodes.UI_PREV_BUTTON)):
                this.getFormattedMessagePart(MessageConfigNodes.UI_PREV_BUTTON_INACTIVE);

        MessageParts nextButton = (finalPageIndex != maximumPageIndex)?
                this.getButton(() -> {
                    BrowseablePageInterface newInterface = this.yieldPage(finalPageIndex + 1);
                    this.interfaceManager.registerInterface(newInterface);
                    newInterface.send();
                }, this.getFormattedMessagePart(MessageConfigNodes.UI_NEXT_BUTTON)):
                this.getFormattedMessagePart(MessageConfigNodes.UI_NEXT_BUTTON_INACTIVE);

        // add one to the displayed page numbers to make them start from 1
        MessageParts pageDisplay = this.getFormattedMessagePart(MessageConfigNodes.F_UI_PAGE_DISPLAY,
                finalPageIndex + 1, maximumPageIndex + 1);

        messagePartsList.add(prevButton);
        messagePartsList.add(pageDisplay);
        messagePartsList.add(nextButton);

        return messagePartsList;
    }

    /**
     * Get the body of the browseable table.
     * @param finalPageIndex processed page index number(should not be out of range)
     * @return
     */
    private static MessagePartsList getTableBody(final int finalPageIndex, final ArrayList<MessagePartsList> entryList) {
        int beginEntryIndex = ENTRY_PER_PAGE * finalPageIndex;
        int lastEntryIndex = Math.min(entryList.size(), beginEntryIndex + ENTRY_PER_PAGE);
        List<MessagePartsList> displayList = entryList.subList(beginEntryIndex, lastEntryIndex);

        MessagePartsList messagePartsList = new MessagePartsList();
        displayList.forEach(messagePartsList::addLine);

        // add blank lines
        int blankLineNumber = ENTRY_PER_PAGE - displayList.size();
        for (int i = 0; i < blankLineNumber; i++) {
            messagePartsList.addLine("");
        }

        return messagePartsList;
    }

    /**
     * This method only returns null in subclasses of {@link BrowseablePageInterface}
     * Functionality of this method is replaced with {@link BrowseablePageInterface#getEntryList}
     * @return null
     */
    @Override
    protected final MessagePartsList getBodyMessages() {
        return null;
    }

    @Override
    protected final MessagePartsList constructInterfaceMessages() {
        MessageParts header = this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER);

        ArrayList<MessagePartsList> entryList = this.getEntryList();
        final int maximumPageIndex = (int) Math.floor(entryList.size() * 1.0 / ENTRY_PER_PAGE);
        final int roundedPageIndex = Math.min(Math.max(0, this.requestedPageIndex), maximumPageIndex);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(header);
        messagePartsList.addAll(getHeading());
        messagePartsList.addAll(getTableBody(roundedPageIndex, entryList));
        messagePartsList.addLine(getBrowseButtonLine(roundedPageIndex, maximumPageIndex));
        messagePartsList.add(footer);

        return messagePartsList;
    }
}
