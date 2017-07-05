package com.github.kory33.signvote.exception.data;

import lombok.Getter;

/**
 * Represents an exception thrown when a piece of data saved by SignVote is broken.
 */
abstract public class InvalidDataException extends Exception {
    @Getter private final String message;

    protected InvalidDataException(String message) {
        this.message = message;
    }
}
