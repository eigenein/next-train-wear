package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.wearable.view.WearableRecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.api.JourneyOptionStatus
import me.eigenein.nexttrainwear.api.JourneyOptionsResponse
import me.eigenein.nexttrainwear.api.nsApiInstance
import me.eigenein.nexttrainwear.data.Route
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

/**
 * Used to display possible routes.
 */
class RoutesAdapter(val usingLocation: Boolean, val routes: List<Route>)
    : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    override fun getItemCount(): Int = routes.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(routes[position])
    override fun onViewAttachedToWindow(holder: ViewHolder) = holder.onAttached()
    override fun onViewDetachedFromWindow(holder: ViewHolder) = holder.onDetached()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val disposable = CompositeDisposable()

        private val gpsStatusImageView = itemView.findViewById(R.id.item_route_gps_status_image_view) as ImageView
        private val journeyOptionsRecyclerView = itemView.findViewById(R.id.item_route_recycler_view) as WearableRecyclerView
        private val progressView = itemView.findViewById(R.id.item_route_progress_layout)!!
        private val departureTextView = itemView.findViewById(R.id.item_route_departure_text) as TextView
        private val destinationTextView = itemView.findViewById(R.id.item_route_destination_text) as TextView

        private lateinit var route: Route

        private var response: JourneyOptionsResponse? = null

        init {
            journeyOptionsRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            LinearSnapHelper().attachToRecyclerView(journeyOptionsRecyclerView)
        }

        fun bind(route: Route) {
            this.route = route
            this.response = null
        }

        fun onAttached() {
            val response = this.response
            if (response != null) {
                onResponse(response)
            } else {
                showProgressLayout()
                planJourney()
            }
        }

        fun onDetached() {
            disposable.clear()
        }

        private fun planJourney() {
            disposable.add(
                nsApiInstance.trainPlanner(route.departureStation.code, route.destinationStation.code)
                    .retryWhen { it.flatMap {
                        // TODO: exponential back-off.
                        if (it is HttpException) Observable.timer(RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS)
                        else Observable.error(it)
                    } }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { onResponse(it) }
            )
        }

        private fun onResponse(response: JourneyOptionsResponse) {
            this.response = response

            // Exclude cancelled options.
            val journeyOptions = response.options.filter { it.status !in JourneyOptionStatus.HIDDEN }
            Log.d(LOG_TAG, "Possible options: " + journeyOptions.size)

            // Display journey options.
            progressView.visibility = View.GONE
            journeyOptionsRecyclerView.adapter = JourneyOptionsAdapter(usingLocation, route, journeyOptions)
            journeyOptionsRecyclerView.visibility = View.VISIBLE
        }

        private fun showProgressLayout() {
            journeyOptionsRecyclerView.visibility = View.GONE

            gpsStatusImageView.visibility = if (usingLocation) View.GONE else View.VISIBLE
            departureTextView.text = route.departureStation.longName
            destinationTextView.text = route.destinationStation.longName
            progressView.visibility = View.VISIBLE
        }
    }

    companion object {
        private val LOG_TAG = RoutesAdapter::class.java.simpleName
        private const val RETRY_INTERVAL_SECONDS = 5L
    }
}
