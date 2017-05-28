package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false, name = "ReisStop")
class JourneyStop {

    @field:Element(name = "Spoor", required = false)
    var platform: String? = null
}
