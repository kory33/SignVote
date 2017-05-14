package com.github.kory33.signvote.ui.player;

import java.util.ArrayList;

import com.github.kory33.chatgui.command.RunnableInvoker;
import com.github.kory33.signvote.ui.player.defaults.IDefaultBrowseableInterface;
import com.github.ucchyocean.messaging.tellraw.MessageParts;
import lombok.Getter;
import org.bukkit.entity.Player;

import com.github.kory33.chatgui.tellraw.MessagePartsList;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.chatgui.model.player.BrowseablePageInterface;

/**
 * Represents an interface which displays a list of existing sessions
 * @author Kory
 */
public final class ListSessionInterface extends BrowseablePageInterface implements IDefaultBrowseableInterface {
    private final VoteSessionManager voteSessionManager;
    @Getter private final JSONConfiguration messageConfig;

    public ListSessionInterface(Player player, VoteSessionManager voteSessionManager, JSONConfiguration messageConfig,
                                RunnableInvoker runnableInvoker, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, runnableInvoker, interfaceManager, pageIndex);
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
    public MessagePartsList getHeading() {
        MessagePartsList heading = new MessagePartsList();
        heading.addLine(getMessageConfig().getString(MessageConfigNodes.LIST_UI_HEADING));
        return heading;
    }

    @Override
    public String getPrevButton(boolean isActive) {
        return IDefaultBrowseableInterface.super.getPrevButton(isActive);
    }

    @Override
    public String getNextButton(boolean isActive) {
        return IDefaultBrowseableInterface.super.getNextButton(isActive);
    }

    @Override
    public String getPageDisplayComponent(int currentPageNumber, int maxPageNumber) {
        return IDefaultBrowseableInterface.super.getPageDisplayComponent(currentPageNumber, maxPageNumber);
    }

    @Override
    public MessagePartsList getInterfaceHeader() {
        return IDefaultBrowseableInterface.super.getInterfaceHeader();
    }

    @Override
    public MessagePartsList getInterfaceFooter() {
        return IDefaultBrowseableInterface.super.getInterfaceFooter();
    }

}
