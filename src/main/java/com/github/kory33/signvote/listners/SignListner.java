package com.github.kory33.signvote.listners;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
import com.github.kory33.signvote.constants.SignTexts;
import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.manager.VoteSessionManager;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;

public class SignListner implements Listener {
    private final JSONConfiguration messageConfig;
    private final VoteSessionManager voteSessionManager;
    
    public SignListner(SignVote plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.messageConfig = plugin.getMessagesConfiguration();
        this.voteSessionManager = plugin.getVoteSessionManager();
    }
    
    private void rejectSignCreation(SignChangeEvent sign, String reason) {
        for (int index = 0; index < 4; index++) {
            sign.setLine(index, "");
        }
        
        String errorText = messageConfig.getString(SignTexts.SIGN_CREATION_REJECTED_TEXT);
        sign.setLine(1, errorText);
        
        sign.getPlayer().sendMessage(reason);
    }
    
    @EventHandler
    public void onVoteSignCreated(SignChangeEvent sign) {
        Sign interactedSign = (Sign)sign.getBlock().getState();
        
        if (!interactedSign.getLine(0).equals(SignTexts.SIGN_CREATION_TEXT)) {
            return;
        }
        
        if(!sign.getPlayer().hasPermission(PermissionNodes.CREATE_SIGN)) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigurationNodes.MISSING_PERMS));
            return;
        }
        
        String sessionName = sign.getLine(1);
        VoteSession session = this.voteSessionManager.getVoteSession(sessionName);
        
        if (session == null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigurationNodes.SESSION_DOES_NOT_EXIST));
            return;
        }
        
        String pointName = sign.getLine(2);
        if (pointName == "") {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigurationNodes.VOTEPOINT_NAME_EMPTY));
            return;
        }
        
        if (session.getVotePoint(pointName) != null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigurationNodes.VOTEPOINT_ALREADY_EXISTS));
            return;
        }
        
        VotePoint votePoint = new VotePoint(pointName, (Sign)sign.getBlock().getState(), session);
        session.addVotePoint(votePoint);
        
        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT);
    }
}
