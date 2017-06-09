package com.github.kory33.signvote.ui.player.defaults;

import com.github.kory33.signvote.constants.MessageConfigNodes;

/**
 * Interface that provides default implementations for SignVote's browseable interfaces.
 * @author Kory
 */
public interface IDefaultBrowseableInterface extends IDefaultClickableInterface {
    default String getPrevButton(boolean isActive) {
        String color = this.getMessageConfig().getString(isActive ?
                MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR :
                MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR);
        return color + this.getMessageConfig().getString(MessageConfigNodes.UI_PREV_BUTTON);
    }

    default String getNextButton(boolean isActive) {
        String color = this.getMessageConfig().getString(isActive ?
                MessageConfigNodes.UI_ACTIVE_BUTTON_COLOR :
                MessageConfigNodes.UI_INACTIVE_BUTTON_COLOR);
        return color + this.getMessageConfig().getString(MessageConfigNodes.UI_NEXT_BUTTON);
    }

    default String getPageDisplayComponent(int currentPageNumber, int maxPageNumber) {
        return this.getMessageConfig().getFormatted(MessageConfigNodes.F_UI_PAGE_DISPLAY, currentPageNumber, maxPageNumber);
    }
}
