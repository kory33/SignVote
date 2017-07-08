package com.github.kory33.signvote.manager

import com.github.kory33.chatgui.util.collection.BijectiveHashMap
import com.github.kory33.signvote.session.VoteSession
import com.github.kory33.signvote.utils.FileUtils
import com.github.kory33.signvote.vote.VotePoint
import org.bukkit.block.Sign

import java.io.File
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Stream

class VoteSessionManager
/**
 * Construct a vote session manager
 * @param logger logger to which the information should be logged
 * *
 * @param sessionSaveDirectory Directory
 */
(private val logger: Logger, private val sessionSaveDirectory: File) {
    private val sessionMap: BijectiveHashMap<String, VoteSession> = BijectiveHashMap()

    private fun loadSession(sessionDirectory: File) {
        try {
            val session = VoteSession(sessionDirectory)
            this.sessionMap.put(session.name, session)
            this.logger.info("Successfully loaded session '" + session.name + "'")
        } catch (e: Exception) {
            this.logger.log(Level.SEVERE, "Error reading the session directory: " + sessionDirectory.name, e)
        }

    }

    init {
        FileUtils.getFileListStream(sessionSaveDirectory)
                .filter({ it.isDirectory })
                .forEach({ this.loadSession(it) })
    }

    fun addSession(session: VoteSession) {
        this.sessionMap.put(session.name, session)
    }

    /**
     * Save specific session.
     * @param session vote session whose data is to be saved
     */
    private fun saveSession(session: VoteSession) {
        if (!this.sessionMap.getInverse().containsKey(session)) {
            throw IllegalArgumentException("Non-registered session given!")
        }

        val sessionDirectory = File(this.sessionSaveDirectory, session.name)
        if (!sessionDirectory.exists() && !sessionDirectory.mkdir()) {
            this.logger.log(Level.SEVERE, "Could not create directory " + sessionDirectory.absolutePath)
        }

        try {
            session.saveTo(sessionDirectory)
        } catch (e: IOException) {
            this.logger.log(Level.SEVERE, "Error while saving session: ", e)
        }

    }

    /**
     * Save all the sessions.
     */
    fun saveAllSessions() {
        // purge non-existent sessions
        val nonExistentSessionDirs = FileUtils.getFileListStream(sessionSaveDirectory)
                .filter { file -> this.sessionMap[file.name] == null }
        CompletableFuture.runAsync { nonExistentSessionDirs.forEach({ FileUtils.deleteFolderRecursively(it) }) }

        // save all the session data
        sessionMap.getInverse().keys.forEach({ this.saveSession(it) })
    }

    /**
     * Get the vote session from session name
     * @param sessionName get session instance from the session name
     * *
     * @return session instance with the given name, null if no such session exists
     */
    fun getVoteSession(sessionName: String): VoteSession? {
        return this.sessionMap[sessionName]
    }

    /**
     * Get a point corresponding to the given sign
     * @param votepointSign a vote point sign
     * *
     * @return a vote session that is responsible for the given sign
     */
    fun getVoteSession(votepointSign: Sign): VoteSession? {
        return this.sessionMap.values.stream()
                .filter { session -> session.getVotePoint(votepointSign) != null }
                .findFirst().orElse(null)
    }

    /**
     * Get the corresponding vote point from sign.
     * @param sign a vote point sign
     * *
     * @return a vote point instance associated with the sign.
     * * null if the sign is not a vote point.
     */
    fun getVotePoint(sign: Sign): VotePoint? {
        val session = this.getVoteSession(sign) ?: return null
        return session.getVotePoint(sign)
    }

    fun deleteSession(targetVoteSession: VoteSession) {
        this.sessionMap.removeValue(targetVoteSession)
    }

    /**
     * Get a stream containing all the registered votesessions.
     */
    val voteSessionSet: Set<VoteSession>
        get() = this.sessionMap.getInverse().keys
}
