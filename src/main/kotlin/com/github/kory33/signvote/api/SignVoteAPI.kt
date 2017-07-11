package com.github.kory33.signvote.api

import com.github.kory33.signvote.core.SignVote
import org.bukkit.block.Block
import org.bukkit.block.Sign

/**
 * An API of SignVote which may be openly accessed by addons.
 *
 *
 * Please note that you should only access the internals of SignVote plugin
 * only if the desired functionality is not provided by this class or its subclasses.
 *
 *
 * Internal access may damage the data structure of SignVote plugin, hence
 * manipulating SignVote's data should be done with a certain care.
 */
class SignVoteAPI(private val plugin: SignVote) {

    /**
     * Checks if the given block is a SignVote's vote-point
     * @param block target block
     * *
     * @return true if the sign is a vote-point
     */
    fun isSignVoteSign(block: Block?): Boolean {
        if (block == null) {
            return false
        }

        val blockState = block.state as? Sign ?: return false

        return this.plugin.voteSessionManager!!.getVoteSession(blockState) != null
    }
}
