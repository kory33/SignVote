package com.github.kory33.signvote.vote

import com.github.kory33.signvote.exception.data.InvalidLimitDataException
import org.apache.commons.lang.math.NumberUtils

/**
 * An abstract representation of a limit of something.

 *
 *
 * The limit number should either be 0, positive integer or infinity.
 */
data class Limit @JvmOverloads constructor(private val limit: Int? = null) : Comparable<Limit> {
    init {
        if (limit != null && limit < 0) {
            throw IllegalArgumentException("Limit must not be negative.")
        }
    }

    val isInfinite: Boolean
        get() = this.limit == null

    private fun greaterThan(limit: Limit): Boolean {
        if (limit.isInfinite) {
            return false
        }

        return this.isInfinite || this.limit!! > limit.limit!!
    }

    override fun compareTo(other: Limit): Int {
        if (this.greaterThan(other)) {
            return 1
        }

        if (other.greaterThan(this)) {
            return -1
        }

        return 0
    }

    operator fun minus(integer: Int?): Limit {
        if (this.isInfinite || integer == null) {
            return Limit()
        }

        return Limit(Math.max(this.limit!! - integer, 0))
    }

    val isZero: Boolean
        get() = this.limit != null && this.limit == 0

    override fun toString(): String {
        if (this.isInfinite) {
            return INFINITY
        }

        return this.limit!!.toString()
    }

    companion object {
        private val INFINITY = "infinity"

        val zero = Limit(0)

        fun fromString(limitString: String): Limit {
            if (NumberUtils.isNumber(limitString)) {
                return Limit(NumberUtils.createInteger(limitString))
            }

            if (INFINITY == limitString) {
                return Limit()
            }

            throw InvalidLimitDataException(limitString)
        }
    }
}
