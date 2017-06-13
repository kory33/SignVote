package com.github.kory33.signvote.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An util class that handles things related to Blocks
 */
public class BlockUtil {
    private static final Stream<BlockFace> ADJACENT_FACE =
            Stream.of(BlockFace.values()).filter(face -> Stream.of(face.getModX(), face.getModY(), face.getModZ())
                            .mapToInt(Math::abs)
                            .sum() == 1
            );

    /**
     * Get a set of blocks whose face is touching to the given block
     * @param block target block
     * @return set of blocks whose face is touching to the given block
     */
    public static Set<Block> getBlocksAdjacentTo(Block block) {
        return ADJACENT_FACE.map(block::getRelative).collect(Collectors.toSet());
    }
}
