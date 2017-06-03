package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.transform.RegistryMatcher
import java.util.*

object ApiUtils {
    val MATCHER = RegistryMatcher()

    init {
        MATCHER.bind(Date::class.java, DateFormatTransformer::class.java)
        MATCHER.bind(JourneyOptionStatus::class.java, JourneyOptionStatusTransformer::class.java)
    }
}
