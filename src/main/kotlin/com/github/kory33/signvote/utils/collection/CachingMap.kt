package com.github.kory33.signvote.utils.collection

/**
 * A map that returns a newly created object (and retains as a mapped value)
 * when get(Object) method is called and the key is not mapped or is mapped to null.

 *
 *
 * This class can be effectively used instead of a regular map when there is a frequent usage of
 * put method before calling get(Object).
 *

 * @param <K> key of the HashMap
 * *
 * @param <V> container class to be cached
</V></K> */
open class CachingMap<K, out V>(private val valueInstanceSupplier: () -> V) {

    private val map: MutableMap<K, V> = HashMap()

    /**
     * Returns the value to which the specified key is mapped.

     * Unlike [java.util.Map.get] specification, this method does not return null
     * when the mapping from `key` does not already exist. Instead, a new mapping from `key`
     * to a value supplied by the supplier field will be returned.

     * @param key target key
     * *
     * @return value mapped from key, otherwise an instance created by the supplier passed to the constructor
     */
    operator fun get(key: K): V {
        val result = map[key] ?: return this.map.computeIfAbsent(key, { _ -> valueInstanceSupplier()})
        return result
    }

    fun remove(key: K): V? {
        return this.map.remove(key)
    }

    fun toMap(): Map<K, V> {
        return this.map.toMap()
    }
}
