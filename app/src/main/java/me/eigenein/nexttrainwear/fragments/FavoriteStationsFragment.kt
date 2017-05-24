package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.eigenein.nexttrainwear.Preferences
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.adapters.StationCatalogueAdapter

class FavoriteStationsFragment : Fragment() {

    private var recyclerView: WearableRecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        recyclerView = inflater.inflate(R.layout.fragment_favorite_stations, container, false) as WearableRecyclerView
        return recyclerView
    }

    override fun onStart() {
        super.onStart()
        recyclerView!!.adapter = StationCatalogueAdapter(Preferences.getFavoriteStations(activity))
    }

    companion object {

        fun newInstance(): FavoriteStationsFragment {
            return FavoriteStationsFragment()
        }
    }
}
