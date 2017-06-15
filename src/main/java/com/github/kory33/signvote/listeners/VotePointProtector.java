package com.github.kory33.signvote.listeners;

import com.github.kory33.signvote.core.SignVote;
import com.github.kory33.signvote.utils.BlockUtil;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

import java.util.Set;

/**
 * A listener class that aims to protect blocks behind vote-points
 */
public class VotePointProtector implements Listener {
    private final SignVote plugin;

    public VotePointProtector(SignVote plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVotePointBaseBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();

        Set<Block> attachedVotePoints = BlockUtil.getBlocksAdjacentTo(brokenBlock);
        attachedVotePoints.removeIf(block -> {
            // ignore if the block is not a sign
            MaterialData state = block.getState().getData();
            if (!(state instanceof org.bukkit.material.Sign)) {
                return true;
            }

            // ignore if the sign is not attached to the broken block
            org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) state;
            Block attachedBlock = block.getRelative(signMaterial.getAttachedFace());
            if (!attachedBlock.equals(brokenBlock)) {
                return true;
            }

            // ignore if the sign is not a SignVote's vote-point
            return !this.plugin.getAPI().isSignVoteSign(block);
        });

        if (attachedVotePoints.isEmpty()) {
            return;
        }

        event.setCancelled(true);
    }
}
