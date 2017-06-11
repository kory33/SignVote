package com.github.kory33.signvote.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

/**
 * An util class that handles things related to Blocks
 */
public class BlockUtil {
    /**
     * Get a set of blocks whose face is touching to the given block
     * @param block target block
     * @return set of blocks whose face is touching to the given block
     */
    public static Set<Block> getBlocksAdjacentTo(Block block) {
        Set<Block> blockSet = new HashSet<>();
        blockSet.add(block.getRelative(BlockFace.DOWN));
        blockSet.add(block.getRelative(BlockFace.UP));
        blockSet.add(block.getRelative(BlockFace.NORTH));
        blockSet.add(block.getRelative(BlockFace.EAST));
        blockSet.add(block.getRelative(BlockFace.WEST));
        blockSet.add(block.getRelative(BlockFace.SOUTH));
        return blockSet;
    }
}
