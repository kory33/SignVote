package com.github.kory33.signvote.core;

import com.github.kory33.updatenotificationplugin.bukkit.github.GithubUpdateNotifyPlugin;

public class SignVote extends GithubUpdateNotifyPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
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
