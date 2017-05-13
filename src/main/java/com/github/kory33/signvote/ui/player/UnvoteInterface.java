package com.github.kory33.signvote.ui.player;

import java.util.Optional;

import com.github.kory33.signvote.constants.SubCommands;
import com.github.ucchyocean.messaging.tellraw.MessageParts;
import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;

/**
 * Represents an interface which confirms and executes player's un-vote.
 * @author Kory
 */
public final class UnvoteInterface extends PlayerClickableChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;

    private void unVote() {
        if (!this.isValidSession()) {
            return;
        }
        try {
            this.session.getVoteManager().removeVote(this.targetPlayer.getUniqueId(), votePoint);
            targetPlayer.sendMessage(
                    this.messageConfig.getFormatted(MessageConfigNodes.F_UNVOTED, this.votePoint.getName()));
        } catch (VotePointNotVotedException e) {
            this.targetPlayer.sendMessage(this.messageConfig.getString(MessageConfigNodes.NOT_VOTED));
        }

        this.revokeSession();
    }

    public UnvoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig, RunnableHashTable runnableHashTable) {
        super(player, runnableHashTable);

        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
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

    private String getHeading() {
        String votePointName = this.votePoint.getName();
        Optional<Integer> optionalVotedScore = this.session.getVoteManager()
                .getVotedScore(this.targetPlayer.getUniqueId(), votePointName);
        if (!optionalVotedScore.isPresent()) {
            throw new IllegalStateException("Player Unvote Interface has been invoked against a non-voted votepoint!");
        }

        int votedScore = optionalVotedScore.get();

        return messageConfig.getFormatted(MessageConfigNodes.UNVOTE_UI_HEADING, votePointName, votedScore);
    }

    private MessagePartsList getSessionClosedMessage() {
        String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
        return new MessagePartsList(message + "\n");
    }

    private void cancelAction() {
        MessageParts cancelMessage = this.getFormattedMessagePart(MessageConfigNodes.UI_CANCELLED);
        this.cancelAction(cancelMessage.build());
    }

    @Override
    protected MessagePartsList getBodyMessages() {
        if (!this.session.isOpen()) {
            return this.getSessionClosedMessage();
        }

        String defaultButtonMessage = this.messageConfig.getString(MessageConfigNodes.UI_BUTTON);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(this.getHeading());
        messagePartsList.add(this.getButton(this::unVote, defaultButtonMessage));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UNVOTE_UI_COMFIRM));
        messagePartsList.add(this.getButton(this::cancelAction, defaultButtonMessage));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL));

        return messagePartsList;
    }

    @Override
    protected MessagePartsList getInterfaceHeader() {
        return new MessagePartsList(this.getFormattedMessagePart(MessageConfigNodes.UI_HEADER));
    }

    @Override
    protected MessagePartsList getInterfaceFooter() {
        return new MessagePartsList(this.getFormattedMessagePart(MessageConfigNodes.UI_FOOTER));
    }

    @Override
    public String getRunCommandRoot() {
        return SubCommands.ROOT + " " + SubCommands.RUN;
    }
}
