package com.github.kory33.signvote.manager;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.session.VotePointCreationSession;

public class VotePointCreationSessionManager {
    private BijectiveHashMap<Player, VotePointCreationSession> sessionMap;
    
    public VotePointCreationSessionManager(JavaPlugin plugin) {
        this.sessionMap = new BijectiveHashMap<>();
    }

    /**
     * Creates a record of new session.
     * @param creator a player responsible for this session
     * @param targetSign
     */
    public void createNewSession(Player creator, Sign targetSign) {
        if (this.sessionMap.containsKey(creator)) {
            this.sessionMap.removeKey(creator);
        }
        
        VotePointCreationSession session = new VotePointCreationSession(creator, targetSign);
        this.sessionMap.put(creator, session);
    }

    /**
     * Delete the session the given player is responsible for.
     * @param player
     */
    public void deleteSession(Player player) {
        this.sessionMap.removeKey(player);
    }

    /**
     * Get the session which the given player is responsible for.
     * @param player
     * @return
     */
    public VotePointCreationSession getSession(Player player) {
        return this.sessionMap.get(player);
    }
}
