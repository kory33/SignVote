package com.github.kory33.signvote.model;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;

/**
 * An abstract representation of a limit of something
 */
public final class Limit {
    private static final String INFINITY = "infinity";

    @Getter private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    public Limit() {
        this.limit = null;
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

    public String toString() {
        if (this.isInfinite()) {
            return INFINITY;
        }

        return this.limit.toString();
    }
}
