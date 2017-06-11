package com.github.kory33.signvote.api;

import com.github.kory33.signvote.core.SignVote;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 * An API of SignVote which may be openly accessed by addons.
 * <p>
 * Please note that you should only access the internals of SignVote plugin
 * only if the desired functionality is not provided by this class or its subclasses.
 * <p>
 * Internal access may damage the data structure of SignVote plugin, hence
 * manipulating SignVote's data should be done with a certain care.
 */
public class SignVoteAPI {
    private final SignVote plugin;

    public SignVoteAPI(SignVote plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if the given block is a SignVote's vote-point
     * @param block target block
     * @return true if the sign is a vote-point
     */
    public boolean isSignVoteSign(Block block) {
        if (block == null) {
            return false;
        }

        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign)) {
            return false;
        }

        Sign sign = (Sign) blockState;
        return this.plugin.getVoteSessionManager().getVoteSession(sign) != null;
    }
}
