package com.github.kory33.signvote.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.Patterns;
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

        sign.setLine(1, SignTexts.SIGN_CREATION_REJECTED_TEXT);
        sign.getPlayer().sendMessage(reason);
    }

    @EventHandler
    public void onVoteSignCreated(SignChangeEvent sign) {
        if (!sign.getLine(0).equals(SignTexts.SIGN_CREATION_TEXT)) {
            return;
        }

        if(!sign.getPlayer().hasPermission(PermissionNodes.CREATE_SIGN)) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.MISSING_PERMS));
            return;
        }

        String sessionName = sign.getLine(1);
        VoteSession session = this.voteSessionManager.getVoteSession(sessionName);

        if (session == null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.SESSION_DOES_NOT_EXIST));
            return;
        }

        String pointName = sign.getLine(2);
        if (!Patterns.PATTERN_VALID_VP_NAME.matcher(pointName).matches()) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.VOTEPOINT_NAME_INVALID));
            return;
        }

        if (session.getVotePoint(pointName) != null) {
            this.rejectSignCreation(sign, messageConfig.getString(MessageConfigNodes.VOTEPOINT_ALREADY_EXISTS));
            return;
        }

        VotePoint votePoint = new VotePoint(pointName, (Sign)sign.getBlock().getState(), session);
        session.addVotePoint(votePoint);

        sign.setLine(0, SignTexts.REGISTERED_SIGN_TEXT);

        sign.getPlayer().sendMessage(messageConfig.getFormatted(MessageConfigNodes.VOTEPOINT_CREATED, sessionName, votePoint.getName()));
    }

    @EventHandler
    public void onVotePointBreak(BlockBreakEvent event) {
        BlockState state = event.getBlock().getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign)state;
        VotePoint votePoint = this.voteSessionManager.getVotePoint(sign);

        if (votePoint == null) {
            return;
        }

        String sessionName = voteSessionManager.getVoteSession(sign).getName();
        String votepointName = votePoint.getName();

        event.setCancelled(true);
        event.getPlayer().sendMessage(messageConfig.getFormatted(MessageConfigNodes.F_VOTEPOINT_BREAK,
                sessionName, votepointName));
    }
}
