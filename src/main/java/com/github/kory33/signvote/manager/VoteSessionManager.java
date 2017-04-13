package com.github.kory33.signvote.manager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.bukkit.block.Sign;

import com.github.kory33.signvote.collection.BijectiveHashMap;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.utils.FileUtils;

public class VoteSessionManager {
    private final BijectiveHashMap<String, VoteSession> sessionMap;
    
    private final File sessionSaveDirectory;
    
    private final Logger logger;

    private void loadSession(File sessionDirectory) {
        try {
            VoteSession session = new VoteSession(sessionDirectory);
            this.sessionMap.put(session.getName(), session);
            this.logger.info("Successfully loaded session '" + session.getName() + "'");
        } catch(Exception e) {
            this.logger.log(Level.WARNING, "Error reading the session directory: {0}", sessionDirectory.getName());
        }
    }
    
    public VoteSessionManager(Logger logger, File sessionSaveDirectory) {
        this.sessionMap = new BijectiveHashMap<>();
        
        this.sessionSaveDirectory = sessionSaveDirectory;
        this.logger = logger;

        for (File sessionFolder: sessionSaveDirectory.listFiles()) {
            if (!sessionFolder.isDirectory()) {
                continue;
            }
            
            this.loadSession(sessionFolder);
        }
    }
    
    public void addSession(VoteSession session) {
        this.sessionMap.put(session.getName(), session);
    }

    /**
     * Save specific session.
     * @param session
     */
    private void saveSession(VoteSession session) {
        if (!this.sessionMap.getInverse().containsKey(session)) {
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
        // purge non-existent sessions
        Stream<File> nonExistentSessionDirs = FileUtils.getFileListStream(sessionSaveDirectory)
                .filter(file -> this.sessionMap.get(file.getName()) == null);
        CompletableFuture.runAsync(() -> nonExistentSessionDirs.forEach(FileUtils::deleteFolderRecursively));
        
        // save all the session data
        sessionMap.getInverse().keySet()
                .stream()
                .parallel()
                .forEach(this::saveSession);
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
     * Get votepoint corresponding to the given sign
     * @param votepointSign
     * @return
     */
    public VoteSession getVoteSession(Sign votepointSign) {
        for (VoteSession session: this.sessionMap.values()) {
            if (session.getVotePoint(votepointSign) != null) {
                return session;
            }
        }
        
        return null;
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

    public void deleteSession(VoteSession targetVoteSession) {
        this.sessionMap.removeValue(targetVoteSession);
    }
}
