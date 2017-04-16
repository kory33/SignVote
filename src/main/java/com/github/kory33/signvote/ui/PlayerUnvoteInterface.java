package com.github.kory33.signvote.ui;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;

public class PlayerUnvoteInterface extends PlayerChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;
    
    public PlayerUnvoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig) {
        super(player);
        
        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        // TODO construct MessageComponent sent for unvote interface
        return null;
    }
}
