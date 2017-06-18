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
import me.eigenein.nexttrainwear.Cache
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.api.JourneyOptionStatus
import me.eigenein.nexttrainwear.api.JourneyOptionsResponse
import me.eigenein.nexttrainwear.api.nsApiInstance
import me.eigenein.nexttrainwear.data.Route
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Used to display possible routes.
 */
class RoutesAdapter : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    private val routes = ArrayList<Route>()
    private val cache = Cache<String, JourneyOptionsResponse>()

    private var usingLocation = false

    override fun getItemCount(): Int = routes.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(routes[position])
    override fun onViewAttachedToWindow(holder: ViewHolder) = holder.onAttached()
    override fun onViewDetachedFromWindow(holder: ViewHolder) = holder.onDetached()

    fun swap(usingLocation: Boolean, routes: Iterable<Route>) {
        this.usingLocation = usingLocation
        this.routes.clear()
        this.routes.addAll(routes)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val disposable = CompositeDisposable()
        private val adapter = JourneyOptionsAdapter()

        private val gpsStatusImageView: ImageView = itemView.findViewById(R.id.item_route_gps_status_image_view)
        private val journeyOptionsRecyclerView: WearableRecyclerView = itemView.findViewById(R.id.item_route_recycler_view)
        private val progressView: View = itemView.findViewById(R.id.item_route_progress_layout)
        private val departureTextView: TextView = itemView.findViewById(R.id.item_route_departure_text)
        private val destinationTextView: TextView = itemView.findViewById(R.id.item_route_destination_text)

        private lateinit var route: Route

        init {
            journeyOptionsRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            journeyOptionsRecyclerView.adapter = adapter
            LinearSnapHelper().attachToRecyclerView(journeyOptionsRecyclerView)
        }

        fun bind(route: Route) {
            this.route = route
        }

        fun onAttached() {
            val response = cache[route.key]
            if (response != null) {
                Log.d(LOG_TAG, "Cache hit!")
                onResponse(response)
            } else {
                Log.d(LOG_TAG, "Cache miss :(")
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
                        Log.w(LOG_TAG, "Train planner call failed", it)
                        if (it is HttpException || it is SocketTimeoutException || it is UnknownHostException)
                            Observable.timer(RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS)
                        else
                            Observable.error(it)
                    } }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { onResponse(it) }
            )
        }

        private fun onResponse(response: JourneyOptionsResponse) {
            cache.put(route.key, response, RESPONSE_TTL_MILLIS)

            // Exclude cancelled options.
            val journeyOptions = response.options.filter { it.status !in JourneyOptionStatus.HIDDEN }
            Log.d(LOG_TAG, "Possible options: " + journeyOptions.size)

            // Display journey options.
            progressView.visibility = View.GONE
            adapter.swap(usingLocation, route, journeyOptions)
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
        private const val RESPONSE_TTL_MILLIS = 60000L
    }
}
