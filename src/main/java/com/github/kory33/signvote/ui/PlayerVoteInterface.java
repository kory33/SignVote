package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.InvalidScoreVotedException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerVoteInterface extends PlayerInteractiveChatInterface {
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
        String message = messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_HEADING,
                this.votePoint.getName()) + "\n";
        return new MessageParts(message);
    }

    private void vote(int score) {
        if (!this.isValidSession()) {
            return;
        }

        String resultMessage;

        try {
            this.session.vote(this.targetPlayer, votePoint, score);
            resultMessage = this.messageConfig.getFormatted(MessageConfigNodes.VOTED);
        } catch (ScoreCountLimitReachedException exception) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.REACHED_VOTE_SCORE_LIMIT);
        } catch (VotePointAlreadyVotedException exception) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.VOTEPOINT_ALREADY_VOTED);
        } catch (InvalidScoreVotedException exception) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.INVALID_VOTE_SCORE);
        }

        this.targetPlayer.sendMessage(resultMessage);

        this.revokeSession();
    }

    private MessageParts getScoreSelectionLine(int score, Optional<Integer> remaining) {
        String remainingString;
        if (remaining.isPresent()) {
            remainingString = remaining.get().toString();
        } else {
            remainingString = this.messageConfig.getString(MessageConfigNodes.INFINITE);
        }

        String message = this.messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_SCORE_SELECTION, score, remainingString);
        MessageParts messageLine = new MessageParts(message + "\n");
        return messageLine;
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        HashMap<Integer, Optional<Integer>> availableVotePoints = this.session.getAvailableVoteCounts(this.targetPlayer);
        if (availableVotePoints.isEmpty()) {
            String message = this.messageConfig.getString(MessageConfigNodes.MESSAGE_PREFIX) +
                    this.messageConfig.getString(MessageConfigNodes.VOTE_UI_NONE_AVAILABLE);

            MessageComponent messageComponent = new MessageComponent();
            messageComponent.addParts(new MessageParts(message));

            return messageComponent;
        }

        MessageParts header = this.getConfigMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigNodes.UI_FOOTER);

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

        messageList.add(this.getButton(this::cancelAction));
        messageList.add(this.getConfigMessagePart(MessageConfigNodes.UI_CANCEL));

        messageList.add(footer);
        return new MessageComponent(messageList);
    }
}
