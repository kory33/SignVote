package com.github.kory33.signvote.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A helper class that make map operations easier
 */
public class MapUtil {
    public static <K, T, V> Map<K, T> mapValues(Map<K, V> targetMap, Function<V, T> transformer) {
        return targetMap.entrySet().stream().collect(
                HashMap::new,
                (map, element) -> map.put(element.getKey(), transformer.apply(element.getValue())),
                Map::putAll
        );
    }
}
