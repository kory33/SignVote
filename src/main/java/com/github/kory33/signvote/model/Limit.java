package com.github.kory33.signvote.model;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.math.NumberUtils;

/**
 * An abstract representation of a limit of something.
 *
 * <p>
 * The limit number should either be 0, positive integer or infinity.
 */
@EqualsAndHashCode
public final class Limit implements Comparable<Limit> {
    private static final String INFINITY = "infinity";

    private final Integer limit;

    public Limit(Integer limit) {
        if (limit != null && limit < 0) {
            throw new IllegalArgumentException("Limit must not be negative.");
        }
        this.limit = limit;
    }

    public Limit() {
        this(null);
    }

    public static Limit fromString(String limitString) throws InvalidLimitDataException {
        if (NumberUtils.isNumber(limitString)) {
            return new Limit(NumberUtils.createInteger(limitString));
        }

        if (INFINITY.equals(limitString)) {
            return new Limit();
        }

        throw new InvalidLimitDataException(limitString);
    }

    public boolean isInfinite() {
        return this.limit == null;
    }

    private boolean greaterThan(Limit limit) {
        if (limit.isInfinite()) {
            return false;
        }

        return this.isInfinite() || this.limit > limit.limit;
    }

    public int compareTo(Limit limit) {
        if (this.greaterThan(limit)) {
            return 1;
        }

        if (limit.greaterThan(this)) {
            return -1;
        }

        return 0;
    }

    public Limit minus(Limit limit) {
        if (this.isInfinite()) {
            return new Limit();
        }
        if (limit.isInfinite()) {
            return new Limit(0);
        }

        return new Limit(Math.max(this.limit - limit.limit, 0));
    }

    public String toString() {
        if (this.isInfinite()) {
            return INFINITY;
        }

        return this.limit.toString();
    }
}
