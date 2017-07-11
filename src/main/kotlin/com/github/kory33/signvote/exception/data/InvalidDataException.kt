package com.github.kory33.signvote.exception.data

/**
 * Represents an exception thrown when a piece of data saved by SignVote is broken.
 */
abstract class InvalidDataException protected constructor(message: String) : Exception(message)
