package com.github.kory33.signvote.ui.player.model;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.kory33.signvote.ui.ChatInterface;

import lombok.Getter;

/**
 * Represents an abstract player chat interface object
 * @author Kory
 */
public abstract class PlayerChatInterface extends ChatInterface {
    @Getter protected final Player targetPlayer;

    protected PlayerChatInterface(Player player) {
        this.targetPlayer = player;
    }

    public void send() {
        super.send(this.targetPlayer);
    }

    /**
     * @deprecated Use {@link PlayerChatInterface#send()} instead.
     * Any argument passed to this method WILL be ignored and {@link PlayerChatInterface#send()} is then run.
     */
    @Override
    @Deprecated
    public final void send(CommandSender player) {
        this.send();
    }
}
