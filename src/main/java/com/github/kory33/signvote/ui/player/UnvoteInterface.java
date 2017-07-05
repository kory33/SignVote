package com.github.kory33.signvote.ui.player;

import com.github.kory33.chatgui.command.RunnableInvoker;
import com.github.kory33.chatgui.model.player.PlayerClickableChatInterface;
import com.github.kory33.chatgui.tellraw.MessagePartsList;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.defaults.IDefaultClickableInterface;
import com.github.kory33.signvote.vote.VotePoint;
import com.github.ucchyocean.messaging.tellraw.MessageParts;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Represents an interface which confirms and executes player's un-vote.
 * @author Kory
 */
public final class UnvoteInterface extends PlayerClickableChatInterface implements IDefaultClickableInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    @Getter private final JSONConfiguration messageConfig;

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
            JSONConfiguration messageConfig, RunnableInvoker runnableInvoker) {
        super(player, runnableInvoker);

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
        Optional<Integer> optionalVotedScore = this.session.getVoteManager()
                .getVotedScore(this.targetPlayer.getUniqueId(), this.votePoint);
        if (!optionalVotedScore.isPresent()) {
            throw new IllegalStateException("Player Unvote Interface has been invoked against a non-voted votepoint!");
        }

        int votedScore = optionalVotedScore.get();

        return messageConfig.getFormatted(MessageConfigNodes.UNVOTE_UI_HEADING, this.votePoint.getName(), votedScore);
    }

    private MessagePartsList getSessionClosedMessage() {
        String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
        return new MessagePartsList(message + "\n");
    }

    private void cancelAction() {
        super.cancelAction(this.messageConfig.getString(MessageConfigNodes.UI_CANCELLED));
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
    public MessagePartsList getInterfaceHeader() {
        return IDefaultClickableInterface.super.getInterfaceHeader();
    }

    @Override
    public MessagePartsList getInterfaceFooter() {
        return IDefaultClickableInterface.super.getInterfaceFooter();
    }

}
