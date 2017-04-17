package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.exception.InvalidVoteScoreException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerVoteInterface extends PlayerChatInteractiveInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;

    public PlayerVoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig, RunnableHashTable runnableHashTable) {
        super(player, messageConfig, runnableHashTable);
        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
    }

    private MessageParts getHeading() {
        String message = messageConfig.getFormatted(MessageConfigurationNodes.VOTE_UI_HEADING,
                this.votePoint.getName()) + "\n";
        return new MessageParts(message);
    }

    private void vote(int score) {
        String errorMessage;
        try {
            this.session.vote(this.targetPlayer, votePoint, score);
            return;
        } catch (ScoreCountLimitReachedException exception) {
            errorMessage = this.messageConfig.getString(MessageConfigurationNodes.REACHED_VOTE_SCORE_LIMIT);
        } catch (VotePointAlreadyVotedException exception) {
            errorMessage = this.messageConfig.getString(MessageConfigurationNodes.VOTEPOINT_ALREADY_VOTED);
        } catch (InvalidVoteScoreException exception) {
            errorMessage = this.messageConfig.getString(MessageConfigurationNodes.INVALID_VOTE_SCORE);
        }

        this.targetPlayer.sendMessage(errorMessage);
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
            String message = this.messageConfig.getString(MessageConfigurationNodes.MESSAGE_PREFIX) +
                    this.messageConfig.getString(MessageConfigurationNodes.VOTE_UI_NONE_AVAILABLE);

            MessageComponent messageComponent = new MessageComponent();
            messageComponent.addParts(new MessageParts(message));

            return messageComponent;
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
                messageList.add(this.getButton(() -> this.vote(score)));
                messageList.add(this.getScoreSelectionLine(score, availableVotePoints.get(score)));
            });

        messageList.add(footer);
        return new MessageComponent(messageList);
    }
}
