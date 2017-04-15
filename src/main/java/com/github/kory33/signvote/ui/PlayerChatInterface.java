package com.github.kory33.signvote.ui;

import org.bukkit.entity.Player;

public abstract class PlayerChatInterface extends ChatInterface {
	protected final Player targetPlayer;
	
	public PlayerChatInterface(Player player) {
		this.targetPlayer = player;
	}
	
	public void send() {
		super.send(this.targetPlayer);
	}
}