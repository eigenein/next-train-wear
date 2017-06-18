package me.eigenein.nexttrainwear.data

// FIXME: add flag: favorite route / suggested route.
data class Route(val departureStation: Station, val destinationStation: Station) {
    val key: String
        get() = departureStation.code + "-" + destinationStation.code
}
