package com.github.kory33.signvote.ui.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.InvalidScoreVotedException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VoteSessionClosedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * Represents an interface which allows a player to vote to a vote point.
 * @author Kory
 */
public final class VoteInterface extends PlayerClickableChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;

    public VoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig, RunnableHashTable runnableHashTable) {
        super(player, messageConfig, runnableHashTable);
        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
    }

    private MessageParts getHeading() {
        String message = messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_HEADING,
                this.votePoint.getName());
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
        } catch (VoteSessionClosedException e) {
            resultMessage = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
        }

        this.targetPlayer.sendMessage(resultMessage);

        this.revokeSession();
    }

    private String getScoreSelectionLine(int score, Optional<Integer> remaining) {
        String remainingString = remaining.map(Object::toString)
                .orElseGet(() -> this.messageConfig.getString(MessageConfigNodes.INFINITE));

        return this.messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_SCORE_SELECTION, score, remainingString);
    }

    @Override
    protected MessagePartsList getBodyMessages() {
        HashMap<Integer, Optional<Integer>> availableVotePoints = this.session.getAvailableVoteCounts(this.targetPlayer);
        if (availableVotePoints.isEmpty()) {
            String message = this.messageConfig.getString(MessageConfigNodes.VOTE_UI_NONE_AVAILABLE);
            MessagePartsList messagePartsList = new MessagePartsList();
            messagePartsList.addLine(message);
            return messagePartsList;
        }

        if (!this.session.isOpen()) {
            String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
            MessagePartsList messagePartsList = new MessagePartsList();
            messagePartsList.addLine(message);
            return messagePartsList;
        }

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(this.getHeading());

        availableVotePoints
            .keySet()
            .stream()
            .sorted(Comparator.reverseOrder())
            .forEach(score -> {
                messagePartsList.add(this.getButton(() -> this.vote(score)));
                messagePartsList.addLine(this.getScoreSelectionLine(score, availableVotePoints.get(score)));
            });

        messagePartsList.add(this.getButton(this::cancelAction));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL));

        return messagePartsList;
    }
}
