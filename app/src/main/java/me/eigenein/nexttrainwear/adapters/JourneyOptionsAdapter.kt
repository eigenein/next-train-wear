package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import me.eigenein.nexttrainwear.*
import me.eigenein.nexttrainwear.api.JourneyOption
import me.eigenein.nexttrainwear.data.Route
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Used to display possible journey options.
 */
class JourneyOptionsAdapter(val usingLocation: Boolean, val route: Route, val journeyOptions: List<JourneyOption>)
    : RecyclerView.Adapter<JourneyOptionsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = journeyOptions.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_journey_option, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(journeyOptions[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val gpsStatusImageView = itemView.findViewById(R.id.item_journey_option_gps_status_image_view) as ImageView
        val departureStationTextView = itemView.findViewById(R.id.item_journey_option_departure_station_text) as TextView
        val destinationStationTextView = itemView.findViewById(R.id.item_journey_option_destination_station_text) as TextView
        val departureTimeTextView = itemView.findViewById(R.id.item_journey_option_departure_time_text) as TextView
        val arrivalTimeTextView = itemView.findViewById(R.id.item_journey_option_arrival_time_text) as TextView
        val durationTimeTextView = itemView.findViewById(R.id.item_journey_option_duration_text) as TextView
        val clockTimeTextView = itemView.findViewById(R.id.item_journey_option_clock_text) as TextView
        val platformTitleTextView = itemView.findViewById(R.id.item_journey_option_platform_title)!!
        val platformTextView = itemView.findViewById(R.id.item_journey_option_platform_text) as TextView

        private val hoursMinutesTimeFormat: DateFormat = SimpleDateFormat("H:mm", Locale.ENGLISH)

        fun bind(journeyOption: JourneyOption) {
            gpsStatusImageView.visibility = if (usingLocation) View.GONE else View.VISIBLE
            departureStationTextView.text = route.departureStation.longName
            destinationStationTextView.text = route.destinationStation.longName

            val platform = journeyOption.components.getOrNull(0)?.stops?.getOrNull(0)?.platform
            if (platform != null) {
                platformTextView.text = platform.toString()
                platformTitleTextView.visibility = View.VISIBLE
                platformTextView.visibility = View.VISIBLE
            } else {
                platformTitleTextView.visibility = View.GONE
                platformTextView.visibility = View.GONE
            }

            // TODO: display delay flag.
            departureTimeTextView.text = hoursMinutesTimeFormat.format(journeyOption.actualDepartureTime)
            arrivalTimeTextView.text = hoursMinutesTimeFormat.format(journeyOption.actualArrivalTime)
            durationTimeTextView.text =
                (journeyOption.actualArrivalTime - journeyOption.actualDepartureTime).toHmDurationString()
            clockTimeTextView.text =
                (journeyOption.actualDepartureTime - Date()).toHmsDurationString()
        }
    }
}
