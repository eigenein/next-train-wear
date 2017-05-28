package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.data.Route
import me.eigenein.nexttrainwear.api.JourneyOptionsResponse
import me.eigenein.nexttrainwear.api.NsApiInstance

/**
 * Used to display possible routes.
 */
class RoutesAdapter(val routes: List<Route>)
    : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    override fun getItemCount(): Int = routes.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(routes[position])
    override fun onViewRecycled(holder: ViewHolder) = holder.dispose()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val journeyOptionsRecyclerView = itemView.findViewById(R.id.item_route_recycler_view) as WearableRecyclerView
        val progressView = itemView.findViewById(R.id.item_route_progress_layout)!!
        val departureTextView = itemView.findViewById(R.id.item_route_departure_text) as TextView
        val destinationTextView = itemView.findViewById(R.id.item_route_destination_text) as TextView

        lateinit var route: Route

        var response: JourneyOptionsResponse? = null
        var trainPlannerDisposable: Disposable? = null

        init {
            journeyOptionsRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            LinearSnapHelper().attachToRecyclerView(journeyOptionsRecyclerView)
        }

        fun bind(route: Route) {
            this.route = route
            // TODO: should be invalidated by timer.
            val response = this.response
            if (response != null) {
                onResponse(response)
            } else {
                showProgressLayout()
                planJourney()
            }
        }

        fun dispose() {
            trainPlannerDisposable?.dispose()
        }

        private fun planJourney() {
            dispose()
            trainPlannerDisposable = NsApiInstance.trainPlanner(route.departureStation.code, route.destinationStation.code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onResponse(it) }
        }

        private fun onResponse(response: JourneyOptionsResponse) {
            this.response = response
            journeyOptionsRecyclerView.adapter = JourneyOptionsAdapter(route, response.options)
            showJourneysLayout()
        }

        private fun showProgressLayout() {
            journeyOptionsRecyclerView.visibility = View.GONE
            departureTextView.text = route.departureStation.longName
            destinationTextView.text = route.destinationStation.longName
            progressView.visibility = View.VISIBLE
        }

        private fun showJourneysLayout() {
            progressView.visibility = View.GONE
            journeyOptionsRecyclerView.visibility = View.VISIBLE
        }
    }
}
