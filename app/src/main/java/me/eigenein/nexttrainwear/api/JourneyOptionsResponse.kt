package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict=false)
class JourneyOptionsResponse {
    @field:ElementList(name = "ReisMogelijkheid", inline = true)
    lateinit var journeyOptions: List<JourneyOption>
}
