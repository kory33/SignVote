package com.github.kory33.signvote.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.event.HandlerList;

import com.github.kory33.signvote.command.SignVoteCommandExecutor;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.FilePaths;
import com.github.kory33.signvote.listners.QuitListener;
import com.github.kory33.signvote.listners.SignListner;
import com.github.kory33.signvote.manager.VotePointCreationSessionManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

import lombok.Getter;

public class SignVote extends GithubUpdateNotifyPlugin {
    @Getter private VoteSessionManager voteSessionManager;
    @Getter private VotePointCreationSessionManager votePointCreationSessionManager;
    @Getter private JSONConfiguration textsConfiguration;
    
    private boolean isEnabled = false;
    
    private SignVoteCommandExecutor commandExecutor;

    private void loadConfigurations() throws IOException {
        File textSettingsFile = new File(this.getDataFolder(), FilePaths.TEXT_SETTINGS_FILENAME);
        if (!textSettingsFile.exists()) {
            this.saveResource(FilePaths.TEXT_SETTINGS_FILENAME, false);
        }
        this.textsConfiguration = new JSONConfiguration(textSettingsFile);
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
        
        this.voteSessionManager = new VoteSessionManager(this.getLogger(), sessionsDir);
        
        if (this.votePointCreationSessionManager != null) {
            this.votePointCreationSessionManager = new VotePointCreationSessionManager();
        }

        
        new QuitListener(this);
        new SignListner(this);

        this.commandExecutor = new SignVoteCommandExecutor(this);
        this.getCommand("signvote").setExecutor(this.commandExecutor);
        
        this.isEnabled = true;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();

        if (!this.isEnabled) {
            return;
        }
        
        HandlerList.unregisterAll(this);
        
        this.voteSessionManager.saveAllSessions();
        this.voteSessionManager = null;
    }
    
    @Override
    public String getGithubRepository() {
        return "kory33/SignVote";
    }
}
