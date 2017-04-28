package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.exception.VotePointNotVotedException;
import com.github.kory33.signvote.listners.PlayerChatInterceptor;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerUnvoteInterface extends PlayerInteractiveChatInterface {
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
            JSONConfiguration messageConfig, RunnableHashTable runnableHashTable, PlayerChatInterceptor chatInterceptor) {
        super(player, messageConfig, runnableHashTable, chatInterceptor);

        this.session = session;
        this.votePoint = votePoint;
    }

    private MessageParts getHeading() {
        String votePointName = this.votePoint.getName();
        Optional<Integer> optionalVotedScore = this.session.getVoteManager()
                .getVotedScore(this.targetPlayer.getUniqueId(), votePointName);
        if (!optionalVotedScore.isPresent()) {
            throw new IllegalStateException("Player Unvote Interface has been invoked against a non-voted votepoint!");
        }

        int votedScore = optionalVotedScore.get();

        String message = messageConfig.getFormatted(MessageConfigNodes.UNVOTE_UI_HEADING, votePointName, votedScore) + "\n";
        return new MessageParts(message);
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        MessageParts header = this.getConfigMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigNodes.UI_FOOTER);

        ArrayList<MessageParts> messageList = new ArrayList<>();
        messageList.add(header);
        messageList.add(this.getHeading());
        messageList.add(this.getButton(this::unVote));
        messageList.add(this.getConfigMessagePart(MessageConfigNodes.UNVOTE_UI_COMFIRM));
        messageList.add(this.getButton(this::cancelAction));
        messageList.add(this.getConfigMessagePart(MessageConfigNodes.UI_CANCEL));
        messageList.add(footer);

        return new MessageComponent(messageList);
    }
}
