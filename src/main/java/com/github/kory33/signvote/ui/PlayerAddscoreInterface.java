package com.github.kory33.signvote.ui;

import java.util.ArrayList;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;

import com.github.kory33.messaging.tellraw.MessagePartsList;
import com.github.kory33.signvote.collection.RunnableHashTable;
import com.github.kory33.signvote.configurable.JSONConfiguration;
import com.github.kory33.signvote.constants.MagicNumbers;
import com.github.kory33.signvote.constants.MessageConfigNodes;
import com.github.kory33.signvote.constants.PermissionNodes;
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

    private void addScoreLimit() {
        if (score == null) {
            targetPlayer.sendMessage(messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_SCORE_NOT_SET));
            return;
        }

        if (voteLimit == null) {
            voteLimit = MagicNumbers.VOTELIMIT_INFINITY;
        }

        String convertedPermission = permission;
        if (permission == null || permission.isEmpty()) {
            convertedPermission = PermissionNodes.VOTE;
        } else if (permission == "op") {
            convertedPermission = PermissionNodes.VOTE_MORE;
        } else {
            convertedPermission = permission;
        }

        this.session.getVoteScoreCountLimits().addLimit(score, convertedPermission, voteLimit);

        String limitString = voteLimit == MagicNumbers.VOTELIMIT_INFINITY ? "Infinity" : String.valueOf(voteLimit);
        this.targetPlayer.sendMessage(messageConfig.getFormatted(MessageConfigNodes.F_SCORE_LIMIT_ADDED,
                limitString, score, session.getName(), convertedPermission));

        this.revokeSession();
    }

    private MessageParts getHeading() {
        return new MessageParts(messageConfig.getFormatted(MessageConfigNodes.ADDSCORE_UI_HEADING, this.session.getName()));
    }

    private boolean validateLimitInput(String input) {
        try {
            int limit = NumberUtils.createInteger(input);
            if (limit != MagicNumbers.VOTELIMIT_INFINITY && limit <= 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
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
                this::validateLimitInput,
                this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_LIMIT),
                this.getVoteLimitString()
            );

        ArrayList<MessageParts> permissionForm = super.getForm(
                input -> this.permission = input,
                input -> true,
                this.messageConfig.getString(MessageConfigNodes.ADDSCORE_UI_PERMISSION),
                this.permission
            );

        MessageParts submitButton = this.getButton(this::addScoreLimit,
                this.getConfigMessagePart(MessageConfigNodes.ADDSCORE_UI_SUBMIT));

        MessagePartsList messagePartsList = new MessagePartsList();
        messagePartsList.add(header);
        messagePartsList.add(this.getHeading());
        messagePartsList.addAll(scoreForm);
        messagePartsList.addAll(limitForm);
        messagePartsList.addAll(permissionForm);
        messagePartsList.add(submitButton);
        messagePartsList.add(footer);
        return new MessageComponent(messagePartsList);
    }
}
