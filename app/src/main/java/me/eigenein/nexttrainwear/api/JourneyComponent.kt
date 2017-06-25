package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "ReisDeel")
class JourneyComponent {

    @field:Element(name = "RitNummer", required = false)
    var rideNumber: Int? = null

    @field:ElementList(inline = true, empty = false, required = false)
    lateinit var stops: List<JourneyStop>
}
