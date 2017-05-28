package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "ReisDeel")
class JourneyComponent {

    @field:ElementList(inline = true, empty = false, required = false)
    lateinit var stops: List<JourneyStop>
}
