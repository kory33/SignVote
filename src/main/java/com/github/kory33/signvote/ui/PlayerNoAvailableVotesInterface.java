package com.github.kory33.signvote.ui;

import org.bukkit.entity.Player;

import com.github.ucchyocean.messaging.tellraw.MessageComponent;

public class PlayerNoAvailableVotesInterface extends PlayerChatInterface {
	public PlayerNoAvailableVotesInterface(Player player) {
		super(player);
	}

	@Override
	protected MessageComponent constructInterfaceMessages() {
		// TODO implementations
		return null;
	}
}
