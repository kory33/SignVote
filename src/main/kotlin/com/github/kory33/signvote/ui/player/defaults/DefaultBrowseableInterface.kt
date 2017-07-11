package com.github.kory33.signvote.ui.player.defaults

import com.github.kory33.chatgui.command.RunnableInvoker
import com.github.kory33.chatgui.manager.PlayerInteractiveInterfaceManager
import com.github.kory33.chatgui.model.player.IBrowseablePageInterface
import com.github.kory33.signvote.configurable.JSONConfiguration
import com.github.kory33.signvote.constants.MessageConfigNodes
import org.bukkit.entity.Player

/**
 * Interface that provides default implementations for SignVote's browseable interfaces.
 * @author Kory
 */
abstract class DefaultBrowseableInterface : IBrowseablePageInterface, DefaultClickableInterface {
    override val entryPerPage: Int = 10

    final override val interfaceManager: PlayerInteractiveInterfaceManager
    final override val requestedPageIndex: Int

    constructor(player: Player,
                runnableInvoker: RunnableInvoker,
                messageConfig: JSONConfiguration,
                interfaceManager: PlayerInteractiveInterfaceManager,
                requestedPageIndex: Int) : super(player, runnableInvoker, messageConfig) {

        this.interfaceManager = interfaceManager
        this.requestedPageIndex = requestedPageIndex
    }

    constructor(oldInterface: DefaultBrowseableInterface, newIndex: Int)
        : super(oldInterface.targetPlayer, oldInterface.runnableInvoker, oldInterface.messageConfig){

        this.interfaceManager = oldInterface.interfaceManager
        this.requestedPageIndex = newIndex
    }

    override fun getPrevButton(isActive: Boolean): String {
        val color = this.messageConfig.getString(if (isActive)
            MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR
        else
            MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR)
        return color + this.messageConfig.getString(MessageConfigNodes.UI_PREV_BUTTON)
    }

    override fun getNextButton(isActive: Boolean): String {
        val color = this.messageConfig.getString(if (isActive)
            MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR
        else
            MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR)
        return color + this.messageConfig.getString(MessageConfigNodes.UI_NEXT_BUTTON)
    }

    override fun getPageDisplayComponent(currentPageNumber: Int, maxPageNumber: Int): String {
        return this.messageConfig.getFormatted(MessageConfigNodes.F_UI_PAGE_DISPLAY, currentPageNumber, maxPageNumber)
    }
}
