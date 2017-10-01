package me.eigenein.nexttrainwear.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.adapters.StationsAdapter
import me.eigenein.nexttrainwear.utils.getStations
import me.eigenein.nexttrainwear.utils.wearableRecyclerView
import org.jetbrains.anko.*

class StationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return UI {
            wearableRecyclerView(R.style.VerticalRecyclerView) {
                layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
                horizontalPadding = dip(20)
                verticalPadding = dip(60)
                clipToPadding = false
                layoutManager = LinearLayoutManager(activity)
                adapter = StationsAdapter(activity.getStations().toMutableSet())
            }
        }.view
    }
}
