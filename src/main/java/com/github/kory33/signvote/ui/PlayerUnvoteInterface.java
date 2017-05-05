package com.github.kory33.signvote.ui;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerUnvoteInterface extends PlayerClickableChatInterface {
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

    public PlayerUnvoteInterface(Player player, VoteSession session, VotePoint votePoint,
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
    protected MessageComponent constructInterfaceMessages() {
        MessageParts header = this.getConfigMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigNodes.UI_FOOTER);

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.addLine(header);
        messagePartsList.addLine(this.getHeading());
        messagePartsList.add(this.getButton(this::unVote));
        messagePartsList.addLine(this.getConfigMessagePart(MessageConfigNodes.UNVOTE_UI_COMFIRM));
        messagePartsList.add(this.getButton(this::cancelAction));
        messagePartsList.addLine(this.getConfigMessagePart(MessageConfigNodes.UI_CANCEL));
        messagePartsList.add(footer);

        return new MessageComponent(messagePartsList);
    }
}
