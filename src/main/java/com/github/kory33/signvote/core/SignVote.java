package com.github.kory33.signvote.core;

import com.github.kory33.signvote.manager.VotePointCreationSessionManager;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

import lombok.Getter;

public class SignVote extends GithubUpdateNotifyPlugin {
    @Getter private VoteSessionManager voteSessionManager;
    @Getter private VotePointCreationSessionManager votePointCreationSessionManager;
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        this.voteSessionManager = new VoteSessionManager(this);
        this.votePointCreationSessionManager = new VotePointCreationSessionManager(this);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @Override
    public String getGithubRepository() {
        return "kory33/SignVote";
    }
}
