package com.github.kory33.signvote.ui.player;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.manager.PlayerInteractiveInterfaceManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.BrowseablePageInterface;

public final class ListSessionInterface extends BrowseablePageInterface {
    private final VoteSessionManager voteSessionManager;

    public ListSessionInterface(Player player, VoteSessionManager voteSessionManager, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerInteractiveInterfaceManager interfaceManager, int pageIndex) {
        super(player, messageConfiguration, runnableHashTable, interfaceManager, pageIndex);
        this.voteSessionManager = voteSessionManager;
    }

    public ListSessionInterface(ListSessionInterface oldInterface, int newIndex) {
        super(oldInterface, newIndex);
        this.voteSessionManager = oldInterface.voteSessionManager;
    }

    private MessagePartsList getEntry(VoteSession session) {
        String openStateNode = session.isOpen() ? MessageConfigNodes.LIST_UI_SESSION_OPEN : MessageConfigNodes.LIST_UI_SESSION_OPEN;
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
}
