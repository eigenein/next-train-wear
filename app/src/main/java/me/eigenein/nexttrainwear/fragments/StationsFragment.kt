package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.eigenein.nexttrainwear.utils.Preferences
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.adapters.StationsAdapter

class StationsFragment : Fragment() {

    private lateinit var recyclerView: WearableRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recyclerView = inflater.inflate(R.layout.fragment_stations, container, false) as WearableRecyclerView
        recyclerView.adapter = StationsAdapter(Preferences.getStations(activity).toMutableSet())
        return recyclerView
    }
}
