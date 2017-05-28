package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.wearable.view.WearableRecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.Route
import me.eigenein.nexttrainwear.api.NsApiInstance
import java.util.concurrent.TimeUnit

/**
 * Used to display possible routes.
 */
class RoutesAdapter(val routes: List<Route>)
    : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    override fun getItemCount(): Int = routes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_route, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(routes[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Scrolls journeys vertically.
         */
        val journeysRecyclerView: WearableRecyclerView =
            itemView.findViewById(R.id.list_item_route_recycler_view) as WearableRecyclerView

        val progressView: View = itemView.findViewById(R.id.list_item_route_progress_view)
        val departureTextView: TextView =
            itemView.findViewById(R.id.list_item_route_departure_text) as TextView
        val destinationTextView: TextView =
            itemView.findViewById(R.id.list_item_route_destination_text) as TextView

        var trainPlannerDisposable: Disposable? = null

        init {
            journeysRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            LinearSnapHelper().attachToRecyclerView(journeysRecyclerView)
        }

        fun bind(route: Route) {
            journeysRecyclerView.visibility = View.GONE
            progressView.visibility = View.VISIBLE
            departureTextView.text = route.departureStation.longName
            destinationTextView.text = route.destinationStation.longName

            // FIXME: debounce requests.
            trainPlannerDisposable?.dispose()
            trainPlannerDisposable = NsApiInstance.trainPlanner(route.departureStation.code, route.destinationStation.code)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    progressView.visibility = View.GONE
                    journeysRecyclerView.visibility = View.VISIBLE
                }
        }
    }
}
