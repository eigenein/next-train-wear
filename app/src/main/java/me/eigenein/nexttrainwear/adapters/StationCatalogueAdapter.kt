package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton

import me.eigenein.nexttrainwear.Preferences
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.Station
import me.eigenein.nexttrainwear.Stations

class StationCatalogueAdapter(private val checkedStations: MutableSet<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position != 0) R.layout.list_item_station else R.layout.list_item_stations_hint
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.list_item_station) StationViewHolder(view) else DummyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != 0) {
            (holder as StationViewHolder).bind(Stations.STATIONS[position - 1])
        }
    }

    override fun getItemCount(): Int {
        return Stations.STATIONS.size + 1
    }

    private class DummyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class StationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

        private var station: Station? = null
        private val checkBox: CheckBox = itemView.findViewById(R.id.list_item_station_checkbox) as CheckBox

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
                checkedStations.add(station!!.code)
            } else {
                checkedStations.remove(station!!.code)
            }
            Preferences.setFavoriteStations(checkBox.context, checkedStations)
        }
    }
}
