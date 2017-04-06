package com.github.kory33.signvote.listners;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.constants.SignTexts;
import com.github.kory33.signvote.core.SignVote;

public class SignListner implements Listener {
    private SignVote plugin;
    
    public SignListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
    }
    
    @EventHandler
    public void onVoteSignCreated(SignChangeEvent sign) {
        Sign interactedSign = (Sign)sign.getBlock().getState();
        
        if (!interactedSign.getLine(0).equals(SignTexts.SIGN_CREATION_TEXT)) {
            return;
        }
        
        if(!sign.getPlayer().hasPermission(PermissionNodes.CREATE_SIGN)) {
            return;
        }
        
        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT);
        
        this.plugin.getVotePointCreationSessionManager().createNewSession(sign.getPlayer(), interactedSign);
    }
}
