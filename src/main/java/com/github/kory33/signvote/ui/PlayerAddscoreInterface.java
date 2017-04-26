package com.github.kory33.signvote.ui;

import java.util.ArrayList;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MagicNumbers;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.listners.PlayerChatInterceptor;
import com.github.kory33.signvote.session.VoteSession;
import com.github.ucchyocean.messaging.tellraw.MessageComponent;
import com.github.ucchyocean.messaging.tellraw.MessageParts;

public class PlayerAddscoreInterface extends PlayerInteractiveChatInterface {
    private final VoteSession session;
    private Integer score;
    private Integer voteLimit;
    private String permission;

    public PlayerAddscoreInterface(Player player, VoteSession session, JSONConfiguration messageConfiguration,
            RunnableHashTable runnableHashTable, PlayerChatInterceptor chatInterceptor) {
        super(player, messageConfiguration, runnableHashTable, chatInterceptor);
        this.session = session;
    }

    private String getVoteLimitString() {
        if (this.voteLimit == null) {
            return null;
        }

        if (this.voteLimit == MagicNumbers.VOTELIMIT_INFINITY) {
            return this.messageConfig.getString(MessageConfigNodes.INFINITE);
        }

        return this.voteLimit.toString();
    }

    private MessageParts getHeading() {
        return new MessageParts(messageConfig.getFormatted(MessageConfigNodes.ADDSCORE_UI_HEADING, this.session.getName()));
    }

    @Override
    protected MessageComponent constructInterfaceMessages() {
        MessageParts header = this.getConfigMessagePart(MessageConfigNodes.UI_HEADER);
        MessageParts footer = this.getConfigMessagePart(MessageConfigNodes.UI_FOOTER);

        ArrayList<MessageParts> scoreForm = super.getForm(
                input -> this.score = NumberUtils.createInteger(input),
                NumberUtils::isNumber,
                this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_SCORE),
                score == null ? null : score.toString()
            );

        ArrayList<MessageParts> limitForm = super.getForm(
                input -> this.voteLimit = NumberUtils.createInteger(input),
                NumberUtils::isNumber,
                this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_LIMIT),
                this.getVoteLimitString()
            );

        ArrayList<MessageParts> permissionForm = super.getForm(
                input -> this.permission = input,
                input -> true,
                this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_PERMISSION),
                this.permission
            );

        ArrayList<MessageParts> messageParts = new ArrayList<>();
        messageParts.add(header);
        messageParts.add(this.getHeading());
        messageParts.addAll(scoreForm);
        messageParts.addAll(limitForm);
        messageParts.addAll(permissionForm);
        messageParts.add(footer);
        return new MessageComponent(messageParts);
    }
}
