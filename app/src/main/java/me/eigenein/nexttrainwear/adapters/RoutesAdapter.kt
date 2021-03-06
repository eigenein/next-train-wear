package me.eigenein.nexttrainwear.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.wearable.view.WearableRecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import me.eigenein.nexttrainwear.Globals
import me.eigenein.nexttrainwear.R
import me.eigenein.nexttrainwear.api.JourneyOptionStatus
import me.eigenein.nexttrainwear.api.JourneyOptionsResponse
import me.eigenein.nexttrainwear.data.Route
import me.eigenein.nexttrainwear.utils.bundle
import me.eigenein.nexttrainwear.utils.hide
import me.eigenein.nexttrainwear.utils.show
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Used to display possible routes.
 */
class RoutesAdapter : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    private val routes = arrayListOf<Route>()

    private var usingLocation = false

    override fun getItemCount(): Int = routes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(routes[position])

    override fun onViewRecycled(holder: ViewHolder) = holder.dispose()

    override fun getItemViewType(position: Int) = VIEW_TYPE

    fun swap(usingLocation: Boolean, routes: Iterable<Route>) {
        this.usingLocation = usingLocation
        this.routes.clear()
        this.routes.addAll(routes)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val journeyOptionsRecyclerView: WearableRecyclerView = itemView.findViewById(R.id.item_route_recycler_view)

        private val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        private val adapter = JourneyOptionsAdapter()
        private val disposable = CompositeDisposable()
        private val analytics = FirebaseAnalytics.getInstance(itemView.context)

        private val gpsStatusImageView: ImageView = itemView.findViewById(R.id.item_route_gps_status_image_view)

        private val progressView: View = itemView.findViewById(R.id.item_route_progress_layout)
        private val departureTextView: TextView = itemView.findViewById(R.id.item_route_departure_text)
        private val destinationTextView: TextView = itemView.findViewById(R.id.item_route_destination_text)
        private val noTrainsView: View = itemView.findViewById(R.id.fragment_trains_no_trains_layout)
        private val noTrainsTextView: TextView = itemView.findViewById(R.id.fragment_trains_no_trains_text)

        private lateinit var route: Route

        init {
            journeyOptionsRecyclerView.layoutManager = layoutManager
            journeyOptionsRecyclerView.adapter = adapter
            journeyOptionsRecyclerView.setHasFixedSize(true)
            LinearSnapHelper().attachToRecyclerView(journeyOptionsRecyclerView)
        }

        fun bind(route: Route) {
            dispose()

            this.route = route

            val response = Globals.JOURNEY_OPTIONS_RESPONSE_CACHE[route.key]
            if (response != null) {
                onResponse(response)
            } else {
                showProgressLayout()
                refreshJourneyOptions()
            }
        }

        fun refreshJourneyOptions() {
            Log.i(LOG_TAG, "Planning journey: " + route.key)
            disposable.add(
                Globals.NS_API
                    .trainPlanner(route.departureStation.code, route.destinationStation.code)
                    .retryWhen { it.flatMap {
                        Log.w(LOG_TAG, "Train planner call failed", it)
                        if (it is HttpException || it is IOException)
                            Flowable.timer(RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS)
                        else
                            Flowable.error(it)
                    } }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Globals.JOURNEY_OPTIONS_RESPONSE_CACHE[route.key] = it
                        onResponse(it)
                    }
            )
            analytics.logEvent("call_train_planner", bundle {
                putString("departure_name", route.departureStation.longName)
                putString("destination_name", route.destinationStation.longName)
                putString("route_key", route.key)
            })
        }

        fun dispose() {
            disposable.clear()
        }

        private fun onResponse(response: JourneyOptionsResponse) {
            // Exclude cancelled options.
            val journeyOptions = response.options
                .filter { it.status !in JourneyOptionStatus.HIDDEN }
                .sortedBy { it.actualDepartureTime }
            Log.d(LOG_TAG, "Journey options: " + journeyOptions.size)

            // Display journey options.
            progressView.hide()
            if (journeyOptions.isNotEmpty()) {
                val position = layoutManager.findFirstVisibleItemPosition()
                val scrollToDate =
                    if (position != RecyclerView.NO_POSITION) adapter[position].plannedDepartureTime
                    else Date()
                adapter.swap(usingLocation, route, journeyOptions)
                noTrainsView.hide()
                journeyOptionsRecyclerView
                    .show()
                    .scrollToPosition(journeyOptions.indexOfFirst { it.actualDepartureTime >= scrollToDate })
            } else {
                @Suppress("DEPRECATION")
                noTrainsTextView.text = Html.fromHtml(itemView.resources.getString(
                    R.string.fragment_trains_no_trains,
                    route.departureStation.longName,
                    route.destinationStation.longName
                ))
                journeyOptionsRecyclerView.hide()
                noTrainsView.show()
            }
        }

        private fun showProgressLayout() {
            journeyOptionsRecyclerView.hide()
            noTrainsView.hide()

            gpsStatusImageView.visibility = if (usingLocation) View.GONE else View.VISIBLE
            departureTextView.text = route.departureStation.longName
            destinationTextView.text = route.destinationStation.longName
            progressView.show()
        }
    }

    companion object {
        const val VIEW_TYPE = 0

        private val LOG_TAG = RoutesAdapter::class.java.simpleName

        private const val RETRY_INTERVAL_SECONDS = 5L // FIXME: exponential backoff.
    }
}
