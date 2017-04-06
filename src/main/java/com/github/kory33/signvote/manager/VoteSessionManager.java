package com.github.kory33.signvote.manager;

import java.io.File;
import java.util.logging.Level;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.session.VoteSession;

public class VoteSessionManager {
    private BijectiveHashMap<String, VoteSession> sessionMap;
    private final SignVote plugin;
    private final File sessionSaveDirectory;

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
        this.sessionSaveDirectory = plugin.getSessionsDirectory();

        for (File sessionFolder: this.sessionSaveDirectory.listFiles()) {
            if (!sessionFolder.isDirectory()) {
                continue;
            }
            
            this.loadSession(sessionFolder);
        }
    }

    public void saveSession(VoteSession session) {
        if (!this.sessionMap.containsKey(session)) {
            throw new IllegalArgumentException("Non-registered session given!");
        }
        
        File sessionDirectory = new File(this.sessionSaveDirectory, session.getName());
        if (!sessionDirectory.exists()) {
            sessionDirectory.mkdir();
        }
        
        session.saveTo(sessionDirectory);
    }
    
    public void saveAllSessions() {
        for (VoteSession session: sessionMap.values()) {
            this.saveSession(session);
        }
    }
    
    public BijectiveHashMap<String, VoteSession> getSessionMap() {
        return new BijectiveHashMap<>(this.sessionMap);
    }
}
