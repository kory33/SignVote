package com.github.kory33.signvote.ui.player;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.ui.player.model.PlayerClickableChatInterface;

public final class UnvoteInterface extends PlayerClickableChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;

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
        super(player, messageConfig, runnableHashTable);

        this.session = session;
        this.votePoint = votePoint;
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

    @Override
    protected MessagePartsList getBodyMessages() {
        if (!this.session.isOpen()) {
            String message = this.messageConfig.getString(MessageConfigNodes.VOTE_SESSION_CLOSED);
            MessagePartsList messagePartsList = new MessagePartsList();
            messagePartsList.addLine(message);
            return messagePartsList;
        }

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(this.getHeading());
        messagePartsList.add(this.getButton(this::unVote));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UNVOTE_UI_COMFIRM));
        messagePartsList.add(this.getButton(this::cancelAction));
        messagePartsList.addLine(this.getFormattedMessagePart(MessageConfigNodes.UI_CANCEL));

        return messagePartsList;
    }
}
