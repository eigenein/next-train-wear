package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.util.*

@Root(strict = false, name = "ReisMogelijkheid")
class JourneyOption {

    @field:Element(name = "AantalOverstappen", required = false)
    var numberOfTransfers: Int = 0

    @field:Element(name = "GeplandeReisTijd")
    lateinit var plannedDuration: String

    @field:Element(name = "ActueleReisTijd")
    lateinit var actualDuration: String

    @field:Element(name = "Optimaal", required = false)
    var isOptimal: Boolean = false

    @field:Element(name = "VertrekVertraging", required = false)
    var departureDelay: String? = null

    @field:Element(name = "AankomstVertraging", required = false)
    var arrivaldelay: String? = null

    @field:Element(name = "GeplandeVertrekTijd")
    lateinit var plannedDepartureTime: Date

    @field:Element(name = "ActueleVertrekTijd")
    lateinit var actualDepartureTime: Date

    @field:Element(name = "GeplandeAankomstTijd")
    lateinit var plannedArrivalTime: Date

    @field:Element(name = "ActueleAankomstTijd")
    lateinit var actualArrivalTime: Date

    // TODO: hide cancelled options in settings.
    @field:Element(name = "Status", required = false)
    lateinit var status: JourneyOptionStatus

    @field:ElementList(inline = true, empty = false, required = false)
    lateinit var components: List<JourneyComponent>
}
