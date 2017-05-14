package com.github.kory33.signvote.ui.player.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableInvoker;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

import lombok.Getter;

/**
 * A class representing an interface whose buttons at the bottom allow the player to move between pages.
 * @author kory
 *
 */
public abstract class BrowseablePageInterface extends PlayerClickableChatInterface {
    @Setter private int entryPerPage;

    @Getter private int requestedPageIndex;
    protected final PlayerInteractiveInterfaceManager interfaceManager;

    /**
     * Get the list of all entries.
     * The list must be ordered in an appropriate way.
     * Each entry may not contain line ending at the end.
     * @return a list containing entries to be displayed on a page
     */
    protected abstract ArrayList<MessagePartsList> getEntryList();

    /**
     * Create new instance of browseable chat interface with the specified index.
     */
    protected abstract BrowseablePageInterface yieldPage(int pageIndex);

    /**
     * Get the line which is inserted before the table's main body
     * @return heading message
     */
    protected abstract MessagePartsList getHeading();

    /**
     * Get a string representing a button to go to the previous page
     * @param isActive boolean value true if and only if the button is active
     * @return button string
     */
    protected abstract String getPrevButton(boolean isActive);

    /**
     * Get a string representing a button to go to the next page
     * @param isActive boolean value true if and only if the button is active
     * @return button string
     */
    protected abstract String getNextButton(boolean isActive);

    protected abstract String getPageDisplayComponent(int currentPageNumber, int maxPageNumber);

    public BrowseablePageInterface(Player player, RunnableInvoker runnableInvoker,
                                   PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, runnableInvoker);
        this.interfaceManager = interfaceManager;
        this.requestedPageIndex = pageIndex;
        this.entryPerPage = 10;
    }

    /**
     * Get the copy of the new interface with another index specified.
     * No registration to the interface manager is done in this constructor.
     * @param oldInterface old interface instance
     * @param newIndex page number(starts from 0) of the new interface
     */
    public BrowseablePageInterface(BrowseablePageInterface oldInterface, int newIndex) {
        super(oldInterface.getTargetPlayer(), oldInterface.getRunnableInvoker());
        this.interfaceManager = oldInterface.interfaceManager;
        this.requestedPageIndex = newIndex;
        this.entryPerPage = oldInterface.entryPerPage;
    }

    /**
     * Get the body of the browse-able table.
     * @param finalPageIndex processed page index number(should not be out of range)
     * @return list representing the browse button interface component
     */
    private MessagePartsList getBrowseButtonLine(final int finalPageIndex, final int maximumPageIndex) {
        MessagePartsList messagePartsList = new MessagePartsList();

        MessageParts prevButton = (finalPageIndex != 0) ?
                this.getButton(() -> {
                    BrowseablePageInterface newInterface = this.yieldPage(finalPageIndex - 1);
                    this.interfaceManager.registerInterface(newInterface);
                    newInterface.send();
                }, this.getPrevButton(true)):
                new MessageParts(this.getPrevButton(false));

        MessageParts nextButton = (finalPageIndex != maximumPageIndex)?
                this.getButton(() -> {
                    BrowseablePageInterface newInterface = this.yieldPage(finalPageIndex + 1);
                    this.interfaceManager.registerInterface(newInterface);
                    newInterface.send();
                }, this.getNextButton(true)):
                new MessageParts(this.getNextButton(false));

        // add one to the displayed page numbers to make them start from 1
        MessageParts pageDisplay = new MessageParts(this.getPageDisplayComponent(finalPageIndex + 1, maximumPageIndex + 1));

        messagePartsList.add(prevButton);
        messagePartsList.add(pageDisplay);
        messagePartsList.add(nextButton);

        return messagePartsList;
    }

    /**
     * Get the body of the browseable table.
     * @param finalPageIndex processed page index number(should not be out of range)
     * @param entryList a list containing entries to be displayed.
     *                  Entry order will be same as arranged in this list.
     */
    private MessagePartsList getTableBody(final int finalPageIndex, final ArrayList<MessagePartsList> entryList) {
        int beginEntryIndex = this.entryPerPage * finalPageIndex;
        int lastEntryIndex = Math.min(entryList.size(), beginEntryIndex + this.entryPerPage);
        List<MessagePartsList> displayList = entryList.subList(beginEntryIndex, lastEntryIndex);

        MessagePartsList messagePartsList = new MessagePartsList();
        displayList.forEach(messagePartsList::addLine);

        return messagePartsList;
    }

    @Override
    protected final MessagePartsList getBodyMessages() {
        ArrayList<MessagePartsList> entryList = this.getEntryList();
        final int maximumPageIndex = (int) Math.floor(entryList.size() * 1.0 / this.entryPerPage);
        final int roundedPageIndex = Math.min(Math.max(0, this.requestedPageIndex), maximumPageIndex);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addAll(this.getHeading());
        messagePartsList.addAll(this.getTableBody(roundedPageIndex, entryList));
        messagePartsList.addLine(this.getBrowseButtonLine(roundedPageIndex, maximumPageIndex));

        return messagePartsList;
    }
}
