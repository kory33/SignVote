package com.github.kory33.signvote.utils.collection;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A map that returns a newly created object (and retains as a mapped value)
 * when get(Object) method is called and the key is not mapped or is mapped to null.
 *
 * <p>
 * This class can be effectively used instead of a regular map when there is a frequent usage of
 * put method before calling get(Object).
 * </p>
 *
 * @param <K> key of the HashMap
 * @param <V> container class to be cached
 */
public class CachingMap<K, V> {
    private final Supplier<V> valueInstanceSupplier;

    private final Map<K, V> map;

    public CachingMap(Supplier<V> valueSupplier) {
        this.valueInstanceSupplier = valueSupplier;
        this.map = new HashMap<>();
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * Unlike {@link java.util.Map#get(Object)} specification, this method does not return null
     * when the mapping from {@code key} does not already exist. Instead, a new mapping from {@code key}
     * to a value supplied by the supplier field will be returned.
     *
     * @param key target key
     * @return value mapped from key, otherwise an instance created by the supplier passed to the constructor
     */
    public final V get(K key) {
        V result = map.get(key);
        if (result == null) {
            return this.map.computeIfAbsent(key, k -> this.valueInstanceSupplier.get());
        }
        return result;
    }

    public final Optional<V> remove(K key) {
        return Optional.ofNullable(this.map.remove(key));
    }

    public final ImmutableMap<K, V> toImmutableMap() {
        return ImmutableMap.copyOf(this.map);
    }
}
