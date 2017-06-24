package com.github.kory33.signvote.model;

import com.github.kory33.signvote.exception.data.InvalidLimitDataException;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;

/**
 * An abstract representation of a limit of something
 */
public abstract class Limit {
    private static final String INFINITY = "infinity";

    @Getter private final Integer limit;

    public Limit(Integer limit) {
        this.limit = limit;
    }

    public Limit() {
        this.limit = null;
    }

    public Limit(String limitString) throws InvalidLimitDataException {
        if (NumberUtils.isNumber(limitString)) {
            this.limit = NumberUtils.createInteger(limitString);
            return;
        }
        if (INFINITY.equals(limitString)) {
            this.limit = null;
            return;
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
