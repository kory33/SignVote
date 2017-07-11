package com.github.kory33.signvote.listeners

import com.github.kory33.signvote.core.SignVote
import com.github.kory33.signvote.utils.BlockUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

/**
 * A listener class that aims to protect blocks behind vote-points
 */
class VotePointProtector(private val plugin: SignVote) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onVotePointBaseBlockBreak(event: BlockBreakEvent) {
        val brokenBlock = event.block

        val protectTarget = BlockUtil.getBlocksAdjacentTo(brokenBlock)
                .firstOrNull { block ->
                    val signMaterial = block.state.data as? org.bukkit.material.Sign ?: return@firstOrNull false

                    // ignore if the sign is not attached to the broken block
                    val attachedBlock = block.getRelative(signMaterial.attachedFace)
                    if (attachedBlock != brokenBlock) {
                        return@firstOrNull false
                    }

                    return@firstOrNull this.plugin.api!!.isSignVoteSign(block)
                }

        if(protectTarget != null) {
            event.isCancelled = true
        }
    }
}
