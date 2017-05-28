package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.transform.RegistryMatcher
import java.util.*

object ApiUtils {
    val matcher = RegistryMatcher()

    init {
        matcher.bind(Date::class.java, DateFormatTransformer::class.java)
    }
}
