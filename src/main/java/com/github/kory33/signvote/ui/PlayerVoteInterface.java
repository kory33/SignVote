package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.ClickEventType;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerVoteInterface extends PlayerChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;
    
    public PlayerVoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig) {
        super(player);
        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
    }

    private MessageParts getConfigMessagePart(String configurationNode) {
        return new MessageParts(this.messageConfig.getString(configurationNode));
    }

    private MessageParts getHeading() {
        String message = messageConfig.getFormatted(MessageConfigurationNodes.VOTE_UI_HEADING,
                this.votePoint.getName()) + "\n";
        return new MessageParts(message);
    }
    
    private MessageParts getVoteButton(int voteScore) {
        MessageParts button = this.getConfigMessagePart(MessageConfigurationNodes.UI_BUTTON);
        String runCommand = String.join(" ", "/signvote vote", this.session.getName(), this.votePoint.getName(), String.valueOf(voteScore));
        button.setClickEvent(ClickEventType.RUN_COMMAND, runCommand);
        return button;
    }
    
    private MessageParts getScoreSelectionLine(int score, int remaining) {
        String message = this.messageConfig.getFormatted(MessageConfigurationNodes.VOTE_UI_SCORE_SELECTION, score,
                remaining);
        MessageParts messageLine = new MessageParts(message + "\n");
        return messageLine;
    }
    
    @Override
    protected MessageComponent constructInterfaceMessages() {
        HashMap<Integer, Integer> availableVotePoints = this.session.getAvailableVoteCounts(this.targetPlayer);
        if (availableVotePoints.isEmpty()) {
            (new PlayerNoAvailableVotesInterface(this.targetPlayer)).send();
            return null;
        }

        MessageParts header = this.getConfigMessagePart(MessageConfigurationNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigurationNodes.UI_FOOTER);
        
        ArrayList<MessageParts> messageList = new ArrayList<>();
        messageList.add(header);
        messageList.add(this.getHeading());
        
        availableVotePoints
            .keySet()
            .stream()
            .sorted(Comparator.reverseOrder())
            .forEach(score -> {
                messageList.add(this.getVoteButton(score));
                messageList.add(this.getScoreSelectionLine(score, availableVotePoints.get(score)));
            });
        
        messageList.add(footer);
        return new MessageComponent(messageList);
    }
}
