package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.transform.Transform

class JourneyOptionStatusTransformer : Transform<JourneyOptionStatus> {

    private val STATUS_MAPPING = mapOf(
        "VOLGENS-PLAN" to JourneyOptionStatus.ON_SCHEDULE,
        "GEWIJZIGD" to JourneyOptionStatus.CHANGED,
        "VERTRAAGD" to JourneyOptionStatus.DELAYED,
        "NIEUW" to JourneyOptionStatus.NEW,
        "NIET-OPTIMAAL" to JourneyOptionStatus.NOT_OPTIMAL,
        "NIET-MOGELIJK" to JourneyOptionStatus.NOT_POSSIBLE,
        "PLAN-GEWIJZIGD" to JourneyOptionStatus.PLAN_CHANGED,
        "GEANNULEERD" to JourneyOptionStatus.CANCELLED
    )

    override fun read(value: String) = STATUS_MAPPING[value] ?: JourneyOptionStatus.UNKNOWN
    override fun write(value: JourneyOptionStatus): String = throw NotImplementedError()
}
