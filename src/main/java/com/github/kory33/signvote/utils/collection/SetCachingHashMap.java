package com.github.kory33.signvote.utils.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link CachingHashMap} implementation that caches an empty set
 */
public class SetCachingHashMap<K, SK> extends CachingHashMap<K, Set<SK>> {
    @Override
    protected Set<SK> createEmptyValue() {
        return new HashSet<>();
    }
}
