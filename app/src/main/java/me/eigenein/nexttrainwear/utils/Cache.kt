package me.eigenein.nexttrainwear.utils

import java.util.*

/**
 * Simple non-concurrent cache.
 */
class Cache<in TKey, TValue> {

    private val cache = java.util.HashMap<TKey, Pair<TValue, Long>>()

    fun put(key: TKey, value: TValue, timeToLiveMillis: Long) {
        cache.put(key, Pair(value, java.util.Date().time + timeToLiveMillis))
    }

    operator fun get(key: TKey): TValue? {
        val cachedValue = cache[key]
        if (cachedValue != null) {
            val (value, expiryTime) = cachedValue
            if (java.util.Date().time < expiryTime) {
                return value;
            } else {
                cache.remove(key)
            }
        }
        return null
    }
}
