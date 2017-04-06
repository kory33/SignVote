package com.github.kory33.signvote.core;

import java.io.File;

import org.bukkit.event.HandlerList;

import com.github.kory33.signvote.constants.DirectoryPaths;
import com.github.kory33.signvote.listners.QuitListener;
import com.github.kory33.signvote.listners.SignListner;
import com.github.kory33.signvote.manager.VotePointCreationSessionManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

import lombok.Getter;

public class SignVote extends GithubUpdateNotifyPlugin {
    @Getter private VoteSessionManager voteSessionManager;
    @Getter private VotePointCreationSessionManager votePointCreationSessionManager;

    private void createDataDirectories() {
        File sessionsDir = this.getSessionsDirectory();

        if (!sessionsDir.exists()) {
            sessionsDir.mkdirs();
        }
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        this.createDataDirectories();
        
        this.voteSessionManager = new VoteSessionManager(this);
        this.votePointCreationSessionManager = new VotePointCreationSessionManager();

        new QuitListener(this);
        new SignListner(this);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        
        HandlerList.unregisterAll(this);
        
        this.voteSessionManager.saveAllSessions();
    }
    
    @Override
    public String getGithubRepository() {
        return "kory33/SignVote";
    }

    /**
     * Get the directory location in which the session folders are stored.
     * @return
     */
    public File getSessionsDirectory() {
        return new File(this.getDataFolder(), DirectoryPaths.SESSION_DIR);
    }
}
