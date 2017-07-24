package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.firebase.analytics.FirebaseAnalytics

import me.eigenein.nexttrainwear.utils.Preferences
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.data.Station
import me.eigenein.nexttrainwear.data.Stations
import me.eigenein.nexttrainwear.utils.bundle

class StationsAdapter(private val checkedStations: MutableSet<String>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) =
        if (position != 0) R.layout.item_station else R.layout.item_stations_hint

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.item_station) StationViewHolder(view) else DummyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != 0) {
            (holder as StationViewHolder).bind(Stations.ALL_STATIONS[position - 1])
        }
    }

    override fun getItemCount() = Stations.ALL_STATIONS.size + 1

    private class DummyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

        private val analytics = FirebaseAnalytics.getInstance(itemView.context)
        private val checkBox: CheckBox = itemView.findViewById(R.id.item_station_checkbox)
        private lateinit var station: Station

        init {
            checkBox.setOnCheckedChangeListener(this)
        }

        fun bind(station: Station) {
            this.station = station
            checkBox.text = station.longName
            checkBox.isChecked = checkedStations.contains(station.code)
        }

        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                checkedStations.add(station.code)
            } else {
                checkedStations.remove(station.code)
            }
            Preferences.setStations(checkBox.context, checkedStations)
            analytics.logEvent("on_checked_changed", bundle {
                putString("station_code", station.code)
                putBoolean("is_checked", isChecked)
            })
        }
    }
}
