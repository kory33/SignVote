package com.github.kory33.signvote.ui.player;

import java.util.ArrayList;

import com.github.ucchyocean.messaging.tellraw.MessageParts;
import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

/**
 * Represents an interface which displays a list of existing sessions
 * @author Kory
 */
public final class ListSessionInterface extends BrowseablePageInterface {
    private final VoteSessionManager voteSessionManager;
    private final JSONConfiguration messageConfig;

    public ListSessionInterface(Player player, VoteSessionManager voteSessionManager, JSONConfiguration messageConfig,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, runnableHashTable, interfaceManager, pageIndex);
        this.voteSessionManager = voteSessionManager;
        this.messageConfig = messageConfig;
    }

    private ListSessionInterface(ListSessionInterface oldInterface, int newIndex) {
        super(oldInterface, newIndex);
        this.voteSessionManager = oldInterface.voteSessionManager;
        this.messageConfig = oldInterface.messageConfig;
    }

    /**
     * Get a message formatted with the given array of Object arguments(optional)
     * @param configurationNode configuration node from which the message should be fetched
     * @param objects objects used in formatting the fetched string
     * @return formatted message component
     */
    private MessageParts getFormattedMessagePart(String configurationNode, Object... objects) {
        return new MessageParts(this.messageConfig.getFormatted(configurationNode, objects));
    }

    private MessagePartsList getEntry(VoteSession session) {
        String openStateNode = session.isOpen() ? MessageConfigNodes.LIST_UI_SESSION_OPEN : MessageConfigNodes.LIST_UI_SESSION_CLOSED;
        String sessionState = messageConfig.getString(openStateNode);
        return new MessagePartsList(getFormattedMessagePart(MessageConfigNodes.F_LIST_UI_ENTRY_TEMPLATE, session.getName(), sessionState));
    }

    @Override
    protected ArrayList<MessagePartsList> getEntryList() {
        ArrayList<MessagePartsList> entryList = new ArrayList<>();
        this.voteSessionManager.getVoteSessionStream().map(this::getEntry).forEach(entryList::add);
        return entryList;
    }

    @Override
    protected BrowseablePageInterface yieldPage(int pageIndex) {
        return new ListSessionInterface(this, pageIndex);
    }

    @Override
    protected MessagePartsList getHeading() {
        MessagePartsList heading = new MessagePartsList();
        heading.addLine(messageConfig.getString(MessageConfigNodes.LIST_UI_HEADING));
        return heading;
    }

    @Override
    protected String getPrevButton(boolean isActive) {
        String color = this.messageConfig.getString(isActive ?
                MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR :
                MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR);
        return color + this.messageConfig.getString(MessageConfigNodes.UI_PREV_BUTTON);
    }

    @Override
    protected String getNextButton(boolean isActive) {
        String color = this.messageConfig.getString(isActive ?
                MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR :
                MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR);
        return color + this.messageConfig.getString(MessageConfigNodes.UI_NEXT_BUTTON);
    }

    @Override
    protected String getPageDisplayComponent(int currentPageNumber, int maxPageNumber) {
        return this.messageConfig.getFormatted(MessageConfigNodes.F_UI_PAGE_DISPLAY, currentPageNumber, maxPageNumber);
    }

    @Override
    protected MessagePartsList getInterfaceHeader() {
        return new MessagePartsList(this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER));
    }

    @Override
    protected MessagePartsList getInterfaceFooter() {
        return new MessagePartsList(this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER));
    }
}
