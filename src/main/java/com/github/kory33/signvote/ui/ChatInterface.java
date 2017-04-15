package com.github.kory33.signvote.ui;

import org.bukkit.entity.Player;

import com.github.ucchyocean.messaging.tellraw.MessageComponent;

/**
 * An abstract class which represents the chat UI.
 * An implementation of this class should have 
 * no reference to the player whilst constructing the UI.
 * @author kory33
 */
public abstract class ChatInterface {
	protected abstract MessageComponent constructInterfaceMessages();
	
	/**
	 * Construct the user interface and send to the player.
	 * @param player
	 */
    protected void send(Player player) {
    	this.constructInterfaceMessages().send(player);
    };
}
