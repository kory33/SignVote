package com.github.kory33.signvote.exception.data;

/**
 * Represents an exception thrown when a saved limit data is invalid
 */
public final class InvalidLimitDataException extends InvalidDataException {
    public InvalidLimitDataException(String savedData) {
        super("Limit value was neither a number nor infinity, got" + savedData);
    }

    public InvalidLimitDataException() {
        super("No debug value provided");
    }
}
