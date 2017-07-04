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

    /**
     * Returns the value to which the specified key is mapped.
     *
     * Unlike {@link java.util.Map#get(Object)} specification, this method does not return null
     * when the mapping from {@code key} does not exist.
     *
     * Instead, a new mapping from {@code key} to an empty value is created and
     * that empty value will be returned.
     *
     * @param key target key
     * @return value mapped from key, otherwise an instance created by {@link #createEmptyValue()}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final V get(Object key) {
        try {
            this.putIfAbsent((K) key, this.createEmptyValue());
        } catch (ClassCastException ignored) {}

        return super.get(key);
    }
}
