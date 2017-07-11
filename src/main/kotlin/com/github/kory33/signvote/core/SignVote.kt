package com.github.kory33.signvote.core

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.listener.PlayerChatInterceptor
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.signvote.api.SignVoteAPI
import com.github.kory33.signvote.command.SignVoteCommandExecutor
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.ConfigNodes
import com.github.kory33.signvote.constants.FilePaths
import com.github.kory33.signvote.io.PluginDataAutoSaver
import com.github.kory33.signvote.listeners.PlayerVoteListener
import com.github.kory33.signvote.listeners.SignListener
import com.github.kory33.signvote.listeners.VotePointProtector
import com.github.kory33.signvote.manager.VoteSessionManager
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin
import org.bstats.Metrics
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.HandlerList

import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * Core class of SignVote plugin
 * @author Kory
 */
class SignVote : GithubUpdateNotifyPlugin() {
    var voteSessionManager: VoteSessionManager? = null
        private set
    var messagesConfiguration: JSONConfiguration? = null
        private set
    var configuration: FileConfiguration? = null
        private set
    var runnableInvoker: RunnableInvoker? = null
        private set
    var interfaceManager: PlayerInteractiveInterfaceManager? = null
        private set
    var chatInterceptor: PlayerChatInterceptor? = null
        private set
    var api: SignVoteAPI? = null
        private set

    private var autoSaver: PluginDataAutoSaver? = null

    @Throws(IOException::class)
    private fun loadConfigurations() {
        val messagesSettingsFile = File(this.dataFolder, FilePaths.MESSAGES_SETTINGS_FILENAME)
        if (!messagesSettingsFile.exists()) {
            this.saveResource(FilePaths.MESSAGES_SETTINGS_FILENAME, false)
        }
        this.messagesConfiguration = JSONConfiguration(messagesSettingsFile)

        this.saveDefaultConfig()
        this.configuration = this.config
    }

    private fun enableMetrics() {
        /* unused */
        val metrics = Metrics(this)
    }

    override fun onEnable() {
        super.onEnable()

        // load config, abort if failed
        try {
            loadConfigurations()
        } catch (exception: Exception) {
            this.logger.log(Level.SEVERE, "Failed to read the configuration file! Aborting initialization...", exception)
            return
        }

        // setup session directory
        val sessionsDir = File(this.dataFolder, FilePaths.SESSION_DIR)
        try {
            if (!sessionsDir.exists() && !sessionsDir.mkdirs()) {
                throw IllegalStateException("Directory " + sessionsDir.absolutePath
                        + " could not be created")
            }
        } catch (ignored: Exception) {
            this.logger.log(Level.SEVERE, "Failed to setup session directories! Aborting initialization...")
            return
        }

        // setup runnable invoker
        if (this.runnableInvoker == null) {
            this.runnableInvoker = RunnableInvoker.getRegisteredInstance(this)
        }

        // setup player interface manager
        if (this.interfaceManager == null) {
            this.interfaceManager = PlayerInteractiveInterfaceManager()
        }

        // setup session manager
        this.voteSessionManager = VoteSessionManager(this.logger, sessionsDir)

        // register listeners
        this.chatInterceptor = PlayerChatInterceptor(this)
        SignListener(this)
        PlayerVoteListener(this)
        if (this.configuration!!.getBoolean(ConfigNodes.VOTE_POINT_PROTECTION, true)) {
            VotePointProtector(this)
        }

        // register command
        val commandExecutor = SignVoteCommandExecutor(this)
        this.getCommand("signvote").executor = commandExecutor

        // setup automatic saving routine
        if (this.configuration!!.getBoolean(ConfigNodes.IS_AUTOSAVE_ENABLED, false)) {
            val intervalTicks = this.configuration!!.getInt(ConfigNodes.AUTOSAVE_INTERVAL_TICKS, 2000)
            val shouldLog = this.configuration!!.getBoolean(ConfigNodes.AUTOSAVE_SHOULD_LOG, false)
            this.autoSaver = PluginDataAutoSaver(this, intervalTicks, shouldLog)
        }

        this.enableMetrics()

        this.api = SignVoteAPI(this)
    }

    override fun onDisable() {
        super.onDisable()

        if (!this.isEnabled) {
            return
        }

        HandlerList.unregisterAll(this)

        if (this.autoSaver != null) {
            this.autoSaver!!.stopAutoSaveTask()
            this.autoSaver = null
        }

        this.logger.info("Saving session data")
        saveSessionData()
    }

    /**
     * Reload the plugin
     */
    fun reload() {
        this.onDisable()
        this.reloadConfig()
        this.onEnable()
    }

    fun saveSessionData() {
        this.voteSessionManager!!.saveAllSessions()
    }

    override fun getGithubRepository(): String {
        return "kory33/SignVote"
    }
}
