package com.github.kory33.signvote.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.command.SignVoteCommandExecutor;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.ConfigNodes;
import com.github.kory33.signvote.constants.FilePaths;
import com.github.kory33.signvote.io.PluginDataAutoSaver;
import com.github.kory33.signvote.listners.PlayerVoteListner;
import com.github.kory33.signvote.listners.SignListner;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.utils.LogUtils;
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

import lombok.Getter;

public class SignVote extends GithubUpdateNotifyPlugin {
    @Getter private VoteSessionManager voteSessionManager;
    @Getter private JSONConfiguration messagesConfiguration;
    @Getter private FileConfiguration configuration;
    @Getter private RunnableHashTable runnableHashTable;

    private boolean isEnabled = false;
    private SignVoteCommandExecutor commandExecutor;

    private PluginDataAutoSaver autoSaver;

    private void loadConfigurations() throws IOException {
        File messagesSettingsFile = new File(this.getDataFolder(), FilePaths.MESSAGES_SETTINGS_FILENAME);
        if (!messagesSettingsFile.exists()) {
            this.saveResource(FilePaths.MESSAGES_SETTINGS_FILENAME, false);
        }
        this.messagesConfiguration = new JSONConfiguration(messagesSettingsFile);

        this.saveDefaultConfig();
        this.configuration = this.getConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            loadConfigurations();
        } catch(Exception exception) {
            this.getLogger().log(Level.SEVERE, "Failed to read the configuration file! Aborting initialization...", exception);
            return;
        }

        File sessionsDir = new File(this.getDataFolder(), FilePaths.SESSION_DIR);
        if (!sessionsDir.exists()) {
            sessionsDir.mkdirs();
        }

        if (this.runnableHashTable == null) {
            this.runnableHashTable = new RunnableHashTable(this);
        }

        // add filter for /signvote run command
        LogUtils.addRunCommandFilter();

        this.voteSessionManager = new VoteSessionManager(this.getLogger(), sessionsDir);

        new SignListner(this);
        new PlayerVoteListner(this);

        this.commandExecutor = new SignVoteCommandExecutor(this);
        this.getCommand("signvote").setExecutor(this.commandExecutor);

        if (this.configuration.getBoolean(ConfigNodes.IS_AUTOSAVE_ENABLED, false)) {
            int intervalTicks = this.configuration.getInt(ConfigNodes.AUTOSAVE_INTERVAL_TICKS, 2000);
            boolean shouldLog = this.configuration.getBoolean(ConfigNodes.AUTOSAVE_SHOULD_LOG, false);
            this.autoSaver = new PluginDataAutoSaver(this, intervalTicks, shouldLog);
        }

        this.isEnabled = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (!this.isEnabled) {
            return;
        }

        HandlerList.unregisterAll(this);

        if (this.autoSaver != null) {
            this.autoSaver.stopAutoSaveTask();
        }

        this.getLogger().info("Saving session data");
        saveSessionData();
    }

    /**
     * Reload the plugin
     */
    public void reload() {
        this.onDisable();
        this.reloadConfig();
        this.onEnable();
    }

    public void saveSessionData() {
        this.voteSessionManager.saveAllSessions();
    }

    @Override
    public String getGithubRepository() {
        return "kory33/SignVote";
    }
}
