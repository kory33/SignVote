package com.github.kory33.signvote.utils

internal fun <K, V, T> Map<K, V>.transformValues(transformer: (V) -> T) : Map<K, T>
        = this.mapValues { entry -> transformer(entry.value) }
