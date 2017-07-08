package com.github.kory33.signvote.utils

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * An util class that handles things related to Blocks
 */
object BlockUtil {
    private val adjacentFaces = setOf(
            BlockFace.DOWN,
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.SOUTH
    )

    /**
     * Get a set of blocks whose face is touching to the given block
     * @param block target block
     * *
     * @return set of blocks whose face is touching to the given block
     */
    fun getBlocksAdjacentTo(block: Block): Set<Block> = adjacentFaces.map({ block.getRelative(it) }).toSet()
}
