package me.eigenein.nexttrainwear

import java.util.*

/**
 * Simple non-concurrent cache.
 */
class Cache<in TKey, TValue> {

    private val cache = HashMap<TKey, Pair<TValue, Long>>()

    fun put(key: TKey, value: TValue, timeToLiveMillis: Long) {
        cache.put(key, Pair(value, Date().time + timeToLiveMillis))
    }

    operator fun get(key: TKey): TValue? {
        val cachedValue = cache[key]
        if (cachedValue != null) {
            val (value, expiryTime) = cachedValue
            if (Date().time < expiryTime) {
                return value;
            } else {
                cache.remove(key)
            }
        }
        return null
    }
}
