package me.eigenein.nexttrainwear.utils

import java.util.*

class Cache<TKey, TValue>(val timeToLiveMillis: Long) {

    private val cache = hashMapOf<TKey, Pair<Long, TValue>>()

    operator fun set(key: TKey, value: TValue) {
        cache[key] = Pair(Date().time + timeToLiveMillis, value)
    }

    operator fun get(key: TKey): TValue? {
        val pair = cache[key]
        if (pair != null) {
            val (expiryTime, value) = pair
            if (Date().time < expiryTime) {
                return value
            } else {
                cache.remove(key)
            }
        }
        return null
    }
}
