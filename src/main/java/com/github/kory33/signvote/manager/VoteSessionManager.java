package com.github.kory33.signvote.manager;

import java.io.File;
import java.util.logging.Level;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.session.VoteSession;

public class VoteSessionManager {
    private BijectiveHashMap<String, VoteSession> sessionMap;
    private final SignVote plugin;

    private void loadSession(File sessionDirectory) {
        try {
            VoteSession session = new VoteSession(sessionDirectory);
            this.sessionMap.put(session.getName(), session);
        } catch(Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Error reading the session directory: {0}", sessionDirectory.getName());
        }
    }
    
    public VoteSessionManager(SignVote plugin) {
        this.plugin = plugin;
        
        File sessionsDirectory = plugin.getSessionsDirectory();

        for (File sessionFolder: sessionsDirectory.listFiles()) {
            if (!sessionFolder.isDirectory()) {
                continue;
            }
            
            this.loadSession(sessionFolder);
        }
    }
    
    public BijectiveHashMap<String, VoteSession> getSessionMap() {
        return new BijectiveHashMap<>(this.sessionMap);
    }
}
