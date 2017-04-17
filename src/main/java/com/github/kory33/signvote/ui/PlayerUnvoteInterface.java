package com.github.kory33.signvote.ui;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MessageConfigurationNodes;
import com.github.kory33.signvote.model.VotePoint;
import com.github.kory33.signvote.session.VoteSession;
import com.github.kory33.signvote.utils.tellraw.TellRawUtility;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

import lombok.Setter;

public class PlayerUnvoteInterface extends PlayerChatInterface {
    private final VoteSession session;
    private final VotePoint votePoint;
    private final JSONConfiguration messageConfig;
    private final RunnableHashTable runnableHashTable;

    @Setter private boolean isValid;

    public PlayerUnvoteInterface(Player player, VoteSession session, VotePoint votePoint,
            JSONConfiguration messageConfig, RunnableHashTable runnableHashTable) {
        super(player);

        this.session = session;
        this.votePoint = votePoint;
        this.messageConfig = messageConfig;
        this.runnableHashTable = runnableHashTable;
        this.isValid = true;
    }

    private MessageParts getConfigMessagePart(String configurationNode) {
        return new MessageParts(this.messageConfig.getString(configurationNode));
    }

    private MessageParts getHeading() {
        String votePointName = this.votePoint.getName();
        Optional<Integer> optionalVotedScore = this.session.getVoteManager().getVotedScore(this.targetPlayer, votePointName);
        if (!optionalVotedScore.isPresent()) {
            throw new IllegalStateException("Player Unvote Interface has been invoked against a non-voted votepoint!");
        }

        int votedScore = optionalVotedScore.get();

        String message = messageConfig.getFormatted(MessageConfigurationNodes.UNVOTE_UI_HEADING, votePointName, votedScore) + "\n";
        return new MessageParts(message);
    }

    private MessageParts getUnvoteButton() {
        MessageParts button = this.getConfigMessagePart(MessageConfigurationNodes.UI_BUTTON);
        String command = String.join(" ", "/signvote unvote", this.session.getName(), this.votePoint.getName());
        TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, () -> {
            if (this.isValid) {
                this.targetPlayer.performCommand(command);
            }
            this.setValid(false);
        });
        return button;
    }

    private MessageParts getCancelButton() {
        MessageParts button = this.getConfigMessagePart(MessageConfigurationNodes.UI_BUTTON);
        TellRawUtility.bindRunnableToMessageParts(this.runnableHashTable, button, () -> {
            this.setValid(false);
            String message = this.messageConfig.getString(MessageConfigurationNodes.UI_CANCELLED);
            this.targetPlayer.sendMessage(message);
        });
        return button;
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        MessageParts header = this.getConfigMessagePart(MessageConfigurationNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigurationNodes.UI_FOOTER);

        ArrayList<MessageParts> messageList = new ArrayList<>();
        messageList.add(header);
        messageList.add(this.getHeading());
        messageList.add(this.getUnvoteButton());
        messageList.add(this.getConfigMessagePart(MessageConfigurationNodes.UNVOTE_UI_COMFIRM));
        messageList.add(this.getCancelButton());
        messageList.add(this.getConfigMessagePart(MessageConfigurationNodes.UI_CANCEL));
        messageList.add(footer);

        return new MessageComponent(messageList);
    }
}
