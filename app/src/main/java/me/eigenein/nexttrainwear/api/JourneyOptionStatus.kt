package me.eigenein.nexttrainwear.api

enum class JourneyOptionStatus {
    ON_SCHEDULE,
    CHANGED,
    DELAYED,
    NEW,
    NOT_OPTIMAL,
    NOT_POSSIBLE,
    PLAN_CHANGED,
    CANCELLED,
    UNKNOWN;

    companion object {
        val HIDDEN = setOf(NOT_POSSIBLE, CANCELLED)
    }
}
