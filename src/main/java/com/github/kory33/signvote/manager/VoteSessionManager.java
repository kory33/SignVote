package com.github.kory33.signvote.manager;

import com.github.kory33.chatgui.util.collection.BijectiveHashMap;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.utils.FileUtils;
import org.bukkit.block.Sign;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
            this.logger.log(Level.SEVERE, "Error reading the session directory: " + sessionDirectory.getName(), e);
        }
    }

    /**
     * Construct a vote session manager
     * @param logger logger to which the information should be logged
     * @param sessionSaveDirectory Directory
     */
    public VoteSessionManager(Logger logger, File sessionSaveDirectory) {
        this.sessionMap = new BijectiveHashMap<>();

        this.sessionSaveDirectory = sessionSaveDirectory;
        this.logger = logger;

        File[] dirFiles = sessionSaveDirectory.listFiles();
        assert dirFiles != null;
        for (File sessionFolder: dirFiles) {
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
     * @param session vote session whose data is to be saved
     */
    private void saveSession(VoteSession session) {
        if (!this.sessionMap.getInverse().containsKey(session)) {
            throw new IllegalArgumentException("Non-registered session given!");
        }

        File sessionDirectory = new File(this.sessionSaveDirectory, session.getName());
        if (!sessionDirectory.exists() && !sessionDirectory.mkdir()) {
            this.logger.log(Level.SEVERE, "Could not create directory " + sessionDirectory.getAbsolutePath());
        }

        try {
            session.saveTo(sessionDirectory);
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Error while saving session: ", e);
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
     * @param sessionName get session instance from the session name
     * @return session instance with the given name, null if no such session exists
     */
    public VoteSession getVoteSession(String sessionName) {
        return this.sessionMap.get(sessionName);
    }

    /**
     * Get a point corresponding to the given sign
     * @param votepointSign a vote point sign
     * @return a vote session that is responsible for the given sign
     */
    public VoteSession getVoteSession(Sign votepointSign) {
        return this.sessionMap.values().stream()
                .filter(session -> session.getVotePoint(votepointSign) != null)
                .findFirst().orElse(null);
    }

    /**
     * Get the corresponding vote point from sign.
     * @param sign a vote point sign
     * @return a vote point instance associated with the sign.
     * null if the sign is not a vote point.
     */
    public VotePoint getVotePoint(Sign sign) {
        VoteSession session = this.getVoteSession(sign);
        if (session == null) {
            return null;
        }
        return session.getVotePoint(sign);
    }

    public void deleteSession(VoteSession targetVoteSession) {
        this.sessionMap.removeValue(targetVoteSession);
    }

    /**
     * Get a stream containing all the registered votesessions.
     */
    public Stream<VoteSession> getVoteSessionStream() {
        return this.sessionMap.getInverse().keySet().stream();
    }
}
