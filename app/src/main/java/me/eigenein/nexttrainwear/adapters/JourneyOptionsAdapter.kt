package me.eigenein.nexttrainwear.adapters

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.api.JourneyOption
import me.eigenein.nexttrainwear.api.JourneyOptionStatus
import me.eigenein.nexttrainwear.data.Route
import me.eigenein.nexttrainwear.utils.asFlowable
import me.eigenein.nexttrainwear.utils.minus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Used to display possible journey options.
 */
class JourneyOptionsAdapter : RecyclerView.Adapter<JourneyOptionsAdapter.ViewHolder>() {

    private val journeyOptions = ArrayList<JourneyOption>()

    private var usingLocation = false

    private lateinit var route: Route

    init {
        setHasStableIds(true)
    }

    fun swap(usingLocation: Boolean, route: Route, journeyOptions: Iterable<JourneyOption>) {
        this.usingLocation = usingLocation
        this.route = route
        this.journeyOptions.clear()
        this.journeyOptions.addAll(journeyOptions)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = journeyOptions.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_journey_option, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(journeyOptions[position])
    override fun onViewRecycled(holder: ViewHolder) = holder.unbind()
    override fun getItemId(position: Int): Long =
        journeyOptions[position].components.getOrNull(0)?.rideNumber?.toLong() ?:
        // For an unexpected case there is no ride number.
        journeyOptions[position].plannedDepartureTime.time

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val gpsStatusImageView = itemView.findViewById(R.id.item_journey_option_gps_status_image_view) as ImageView
        private val departureStationTextView = itemView.findViewById(R.id.item_journey_option_departure_station_text) as TextView
        private val destinationStationTextView = itemView.findViewById(R.id.item_journey_option_destination_station_text) as TextView
        private val departureTimeTextView = itemView.findViewById(R.id.item_journey_option_departure_time_text) as TextView
        private val arrivalTimeTextView = itemView.findViewById(R.id.item_journey_option_arrival_time_text) as TextView
        private val durationTimeTextView = itemView.findViewById(R.id.item_journey_option_duration_text) as TextView
        private val clockTextView = itemView.findViewById(R.id.item_journey_option_clock_text) as TextView
        private val platformTitleTextView = itemView.findViewById(R.id.item_journey_option_platform_title)
        private val platformTextView = itemView.findViewById(R.id.item_journey_option_platform_text) as TextView

        private val handler = Handler()
        private val disposable = CompositeDisposable()

        fun bind(journeyOption: JourneyOption) {
            unbind()

            gpsStatusImageView.visibility = if (usingLocation) View.GONE else View.VISIBLE
            departureStationTextView.text = route.departureStation.longName
            destinationStationTextView.text = route.destinationStation.longName

            val platform = journeyOption.components.getOrNull(0)?.stops?.getOrNull(0)?.platform
            if (platform != null) {
                platformTextView.text = platform
                platformTitleTextView.visibility = View.VISIBLE
                platformTextView.visibility = View.VISIBLE
            } else {
                platformTitleTextView.visibility = View.GONE
                platformTextView.visibility = View.GONE
            }

            departureTimeTextView.text = CLOCK_TIME_FORMAT.format(journeyOption.actualDepartureTime)
            arrivalTimeTextView.text = CLOCK_TIME_FORMAT.format(journeyOption.actualArrivalTime)
            durationTimeTextView.text = journeyOption.actualDuration
            clockTextView.setTextColor(if (journeyOption.status != JourneyOptionStatus.DELAYED) WHITE else RED_ACCENT)

            disposable.add(handler.asFlowable(CLOCK_UPDATE_INTERVAL_MILLIS, true).subscribe {
                clockTextView.text = toClockString(journeyOption.actualDepartureTime - Date())
            })
        }

        fun unbind() {
            disposable.clear()
        }

        private fun toClockString(millis: Long): String {
            val (absMillis, sign) = if (millis >= 0) Pair(millis, "") else Pair(-millis, "-")
            val hours = absMillis / 3600000
            val minutes = (absMillis % 3600000) / 60000
            val seconds = (absMillis % 60000) / 1000
            return if (hours == 0L)
                String.format("%s%d:%02d", sign, minutes, seconds)
            else
                String.format("%s%d:%02d:%02d", sign, hours, minutes, seconds)
        }
    }

    companion object {
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val RED_ACCENT = 0xFFFF8880.toInt()

        private const val CLOCK_UPDATE_INTERVAL_MILLIS = 500L

        private val CLOCK_TIME_FORMAT = SimpleDateFormat("H:mm", Locale.ENGLISH)
    }
}
