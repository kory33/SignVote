package com.github.kory33.signvote.ui.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import com.github.kory33.signvote.ui.player.defaults.IDefaultClickableInterface;
import lombok.Getter;
import org.bukkit.entity.Player;

import com.github.kory33.chatgui.tellraw.MessagePartsList;
import com.github.kory33.chatgui.command.RunnableInvoker;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.InvalidScoreVotedException;
import com.github.kory33.signvote.exception.ScoreCountLimitReachedException;
import com.github.kory33.signvote.exception.VotePointAlreadyVotedException;
import com.github.kory33.signvote.exception.VoteSessionClosedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.chatgui.model.player.PlayerClickableChatInterface;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

/**
 * Represents an interface which allows a player to vote to a vote point.
 * @author Kory
 */
public final class VoteInterface extends PlayerClickableChatInterface implements IDefaultClickableInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    @Getter private final JSONConfiguration messageConfig;

    public VoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig, RunnableInvoker runnableInvoker) {
        super(player, runnableInvoker);
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

    /**
     * Get a message formatted with the given array of Object arguments(optional)
     * @param configurationNode configuration node from which the message should be fetched
     * @param objects objects used in formatting the fetched string
     * @return formatted message component
     */
    private MessageParts getFormattedMessagePart(String configurationNode, Object... objects) {
        return new MessageParts(this.messageConfig.getFormatted(configurationNode, objects));
    }

    private void cancelAction() {
        super.cancelAction(this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED));
    }

    private String getScoreSelectionLine(int score, Optional<Integer> remaining) {
        String remainingString = remaining.map(Object::toString)
                .orElseGet(() -> this.messageConfig.getString(MessageConfigNodes.INFINITE));

        return this.messageConfig.getFormatted(MessageConfigNodes.VOTE_UI_SCORE_SELECTION, score, remainingString);
    }

    private MessagePartsList getSessionClosedMessage() {
        String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
        return new MessagePartsList(message + "\n");
    }

    @Override
    protected MessagePartsList getBodyMessages() {
        HashMap<Integer, Optional<Integer>> availableVotePoints = this.session.getAvailableVoteCounts(this.targetPlayer);
        if (availableVotePoints.isEmpty()) {
            return this.getSessionClosedMessage();
        }

        if (!this.session.isOpen()) {
            String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
            MessagePartsList messagePartsList = new MessagePartsList(message);
            messagePartsList.addLine("");
            return messagePartsList;
        }

        String defaultButtonMessage = this.messageConfig.getString(MessageConfigNodes.UI_BUTTON);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(this.getHeading());

        availableVotePoints
            .keySet()
            .stream()
            .sorted(Comparator.reverseOrder())
            .forEach(score -> {
                messagePartsList.add(this.getButton(() -> this.vote(score), defaultButtonMessage));
                messagePartsList.addLine(this.getScoreSelectionLine(score, availableVotePoints.get(score)));
            });

        messagePartsList.add(this.getButton(this::cancelAction, defaultButtonMessage));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL));

        return messagePartsList;
    }

    @Override
    public MessagePartsList getInterfaceHeader() {
        return IDefaultClickableInterface.super.getInterfaceHeader();
    }

    @Override
    public MessagePartsList getInterfaceFooter() {
        return IDefaultClickableInterface.super.getInterfaceFooter();
    }

}
