package com.github.kory33.signvote.session;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class VotePointCreationSession {
    private Player creator;
    private Sign targetSign;
    
    public VotePointCreationSession(Player creator, Sign sign) {
        this.creator = creator;
        this.targetSign = sign;
    }
}
