package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict=false)
class JourneyOptionsResponse {
    @field:ElementList(inline = true, empty = false, required = false)
    lateinit var options: List<JourneyOption>
}
