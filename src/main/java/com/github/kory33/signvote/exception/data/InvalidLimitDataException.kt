package com.github.kory33.signvote.exception.data

/**
 * Represents an exception thrown when a saved limit data is invalid
 */
class InvalidLimitDataException : InvalidDataException {
    constructor(savedData: String) : super("Limit value was neither a number nor infinity, got" + savedData)

    constructor() : super("No debug value provided")
}
