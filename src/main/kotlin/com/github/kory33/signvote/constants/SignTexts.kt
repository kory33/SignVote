package com.github.kory33.signvote.constants

import org.bukkit.ChatColor

/**
 * Collection of texts that are displayed on vote point signs
 * @author Kory
 */
object SignTexts {
    val SIGN_CREATION_TEXT = "[SignVote]"
    val REGISTERED_SIGN_TEXT = ChatColor.AQUA.toString() + "[SignVote]"
    val SIGN_CREATION_REJECTED_TEXT = ChatColor.RED.toString() + "[ERROR!]"
    val DELETED = ChatColor.RED.toString() + "[DELETED]"
}
