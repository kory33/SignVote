package com.github.kory33.signvote.exception

import com.github.kory33.signvote.session.VoteSession

/**
 * Represents an exception thrown when some interaction is attempted against a closed session.
 */
class VoteSessionClosedException @java.beans.ConstructorProperties("session")
constructor(session: VoteSession) : Exception() {
    var session: VoteSession? = null
        internal set

    init {
        this.session = session
    }

    override fun toString(): String {
        return "com.github.kory33.signvote.exception.VoteSessionClosedException(session=" + this.session + ")"
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is VoteSessionClosedException) return false
        val other = o as VoteSessionClosedException?
        if (!other!!.canEqual(this as Any)) return false
        val `this$session` = this.session
        val `other$session` = other.session
        if (if (`this$session` == null) `other$session` != null else `this$session` != `other$session`) return false
        return true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$session` = this.session
        result = result * PRIME + (`$session`?.hashCode() ?: 43)
        return result
    }

    protected fun canEqual(other: Any): Boolean {
        return other is VoteSessionClosedException
    }
}
