package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.Route

/**
 * Used to display possible routes.
 */
class RoutesAdapter(val routes: List<Route>)
    : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    override fun getItemCount(): Int = routes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_destination, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Scrolls journeys vertically.
         */
        val journeysRecyclerView: WearableRecyclerView =
            itemView.findViewById(R.id.list_item_destination_recycler_view) as WearableRecyclerView
        val departureTextView: TextView =
            itemView.findViewById(R.id.list_item_destination_departure_text) as TextView
        val destinationTextView: TextView =
            itemView.findViewById(R.id.list_item_destination_destination_text) as TextView

        init {
            journeysRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            LinearSnapHelper().attachToRecyclerView(journeysRecyclerView)
        }

        fun bind(route: Route) {
            departureTextView.text = route.departureStation.longName
            destinationTextView.text = route.destinationStation.longName
            // TODO: make request, hide progress bar and set journeys adapter.
        }
    }
}
