package com.github.kory33.signvote.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.ui.PlayerClickableChatInterface;

/**
 * A class which holds the association of player onto a single session.
 * No player can have two valid sessions registered in this manager class.
 * @author kory
 *
 */
public class PlayerInteractiveInterfaceManager {
    private final Map<Player, PlayerClickableChatInterface> playerInterfaceMap;
    public PlayerInteractiveInterfaceManager() {
        this.playerInterfaceMap = new HashMap<>();
    }

    /**
     * Register an interface session.
     * Any interface that is already associated with the target player
     * will be revoked and replaced
     * @param interactiveInterface
     */
    public void registerInterface(PlayerClickableChatInterface interactiveInterface) {
        Player targetPlayer = interactiveInterface.getTargetPlayer();
        PlayerClickableChatInterface registeredInterface = this.playerInterfaceMap.get(targetPlayer);
        if (registeredInterface != null) {
            registeredInterface.revokeSession();
        }
        this.playerInterfaceMap.put(targetPlayer, interactiveInterface);
    }
}
