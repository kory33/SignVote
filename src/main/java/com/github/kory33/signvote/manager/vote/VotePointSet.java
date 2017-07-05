package com.github.kory33.signvote.manager.vote;

import com.github.kory33.signvote.vote.VotePoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.HashSet;

/**
 * This class extends {@link HashSet} with a type parameter of {@link VotePoint}.
 *
 * No method is overrode, but a method {@link JsonArray} is added.
 */
/*package-private*/ class VotePointSet extends HashSet<VotePoint> {
    /**
     * Convert this object to a json-array that consists of strings representing name of each vote-point.
     * @return converted object
     */
    JsonArray toNameJsonArray() {
        JsonArray array = new JsonArray();

        this.stream()
                .map(VotePoint::getName)
                .map(JsonPrimitive::new)
                .forEach(array::add);

        return array;
    }
}
