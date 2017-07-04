package com.github.kory33.signvote.utils.collection;

import java.util.HashMap;

/**
 * A map that returns an empty container object (and retains as a mapped value)
 * when get(Object) method is called and the mapping does not or is mapped to null.
 *
 * <p>
 * This class can be effectively used when a user of such a map object
 * has to check key presence and call put method before calling get(Object).
 * </p>
 *
 * @param <K> key of the HashMap
 * @param <V> container class to be cached
 */
public abstract class CachingHashMap<K, V> extends HashMap<K, V> {
    protected abstract V createEmptyValue();

    @Override
    @SuppressWarnings("unchecked")
    public final V get(Object key) {
        try {
            this.putIfAbsent((K) key, this.createEmptyValue());
        } catch (ClassCastException ignored) {}

        return super.get(key);
    }
}
