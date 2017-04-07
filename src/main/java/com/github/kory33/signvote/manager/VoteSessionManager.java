package com.github.kory33.signvote.manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.block.Sign;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

public class VoteSessionManager {
    private BijectiveHashMap<String, VoteSession> sessionMap;
    private final File sessionSaveDirectory;
    
    private final Logger logger;

    private void loadSession(File sessionDirectory) {
        try {
            VoteSession session = new VoteSession(sessionDirectory);
            this.sessionMap.put(session.getName(), session);
        } catch(Exception e) {
            this.logger.log(Level.WARNING, "Error reading the session directory: {0}", sessionDirectory.getName());
        }
    }
    
    public VoteSessionManager(Logger logger, File sessionSaveDirectory) {
        this.sessionSaveDirectory = sessionSaveDirectory;
        this.logger = logger;

        for (File sessionFolder: sessionSaveDirectory.listFiles()) {
            if (!sessionFolder.isDirectory()) {
                continue;
            }
            
            this.loadSession(sessionFolder);
        }
    }

    /**
     * Save specific session.
     * @param session
     */
    public void saveSession(VoteSession session) {
        if (!this.sessionMap.containsKey(session)) {
            throw new IllegalArgumentException("Non-registered session given!");
        }
        
        File sessionDirectory = new File(this.sessionSaveDirectory, session.getName());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdir();
        }
        
        try {
            session.saveTo(sessionDirectory);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Error while saving session: ", e);;
        }
    }
    
    /**
     * Save all the sessions.
     */
    public void saveAllSessions() {
        for (VoteSession session: sessionMap.values()) {
            this.saveSession(session);
        }
    }

    /**
     * Get the vote session from session name
     * @param sessionName
     * @return
     */
    public VoteSession getVoteSession(String sessionName) {
        return this.sessionMap.get(sessionName);
    }

    /**
     * Get the corresponding vote point from sign.
     * @param sign
     * @return
     */
    public VotePoint getVotePoint(Sign sign) {
        for (VoteSession session: this.sessionMap.values()) {
            VotePoint votePoint = session.getVotePoint(sign);
            
            if (votePoint != null) {
                return votePoint;
            }
        }
        
        return null;
    }
}
