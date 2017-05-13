package com.github.kory33.signvote.ui.player.defaults;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.SubCommands;
import org.bukkit.entity.Player;

/**
 * Interface that provides default implementations for SignVote's clickable interfaces
 * @author Kory
 */
public interface IDefaultClickableInterface {
    Player getTargetPlayer();

    JSONConfiguration getMessageConfig();

    /**
     * Get the header line of the interface
     * @return message component list representing the header
     */
    default MessagePartsList getInterfaceHeader() {
        return new MessagePartsList(this.getMessageConfig().getString(MessageConfigNodes.UI_HEADER));
    }

    /**
     * Get the footer line of the interface
     * @return message component list representing the footer
     */
    default MessagePartsList getInterfaceFooter() {
        return new MessagePartsList(this.getMessageConfig().getString(MessageConfigNodes.UI_FOOTER));
    }

    /**
     * Get a root component of the "run command" which is executed when a player clicks on a button.
     * @return run command root string
     */
    default String getRunCommandRoot() {
        return SubCommands.ROOT + " " + SubCommands.RUN;
    }
}
