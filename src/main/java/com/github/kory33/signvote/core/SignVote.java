package com.github.kory33.signvote.core;

import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

public class SignVote extends GithubUpdateNotifyPlugin {
    private VoteSessionManager voteSessionManager;
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        this.voteSessionManager = new VoteSessionManager(this);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @Override
    public String getGithubRepository() {
        return "kory33/SignVote";
    }
    
    public VoteSessionManager getVoteSessionManager() {
        return this.voteSessionManager;
    }
}
