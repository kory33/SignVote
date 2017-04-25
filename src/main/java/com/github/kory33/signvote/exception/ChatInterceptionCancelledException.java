package com.github.kory33.signvote.exception;

import lombok.Getter;

public class ChatInterceptionCancelledException extends Exception {
    private static final long serialVersionUID = 6258631196749680161L;

    @Getter private String reason;

    public ChatInterceptionCancelledException(String reason) {
        this.reason = reason;
    }

    public ChatInterceptionCancelledException() {
        this(null);
    }
}
