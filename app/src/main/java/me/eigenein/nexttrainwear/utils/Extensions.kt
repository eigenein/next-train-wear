package me.eigenein.nexttrainwear.utils

import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.wear.widget.WearableRecyclerView
import android.support.wear.widget.drawer.WearableDrawerLayout
import android.support.wear.widget.drawer.WearableNavigationDrawerView
import android.util.Log
import android.view.View
import android.view.ViewManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import me.eigenein.nexttrainwear.exceptions.GoogleApiClientConnectionFailedException
import me.eigenein.nexttrainwear.exceptions.LocationRequestFailedException
import org.jetbrains.anko.custom.ankoView
import org.simpleframework.xml.transform.RegistryMatcher
import java.util.*

operator fun Date.minus(other: Date): Long = this.time - other.time

/**
 * Make an observable from Google API client builder emitting the built client when connected.
 */
fun GoogleApiClient.Builder.asFlowable(): Flowable<GoogleApiClient> {
    return Flowable.create({ emitter ->
        var client: GoogleApiClient? = null
        client = this
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {
                    emitter.onNext(client!!)
                    emitter.onComplete()
                }
                override fun onConnectionSuspended(i: Int) = Unit
            })
            .addOnConnectionFailedListener { emitter.onError(GoogleApiClientConnectionFailedException(it.errorMessage)) }
            .build()
        client.connect()
    }, BackpressureStrategy.ERROR)
}

/**
 * Make an observable from the location request emitting location updates.
 */
fun LocationRequest.asFlowable(googleApiClient: GoogleApiClient): Flowable<Location> {
    return Flowable.create({ emitter ->
        val listener = { location: Location ->
            Log.d("LocationRequest.asFlowa", String.format("%s %s", location, Date(location.time)))
            emitter.onNext(location)
        }
        LocationServices.FusedLocationApi
            .requestLocationUpdates(googleApiClient, this, listener)
            .setResultCallback {
                if (!it.isSuccess) {
                    emitter.onError(LocationRequestFailedException(it.statusMessage))
                }
            }
        emitter.setCancellable { LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener) }
    }, BackpressureStrategy.BUFFER)
}

fun Handler.asFlowable(delayMillis: Long): Flowable<Unit> {
    return Flowable.create({
        val runnable = object : Runnable {
            override fun run() {
                try {
                    it.onNext(Unit)
                } finally {
                    scheduleNext()
                }
            }

            fun scheduleNext() = this@asFlowable.postDelayed(this, delayMillis)
        }

        it.setCancellable { this.removeCallbacks(runnable) }
        runnable.scheduleNext()
    }, BackpressureStrategy.DROP)
}

fun <T : View> T.show(): T {
    this.visibility = View.VISIBLE
    return this
}

fun <T : View> T.hide(): T {
    this.visibility = View.GONE
    return this
}

fun <TViewHolder : RecyclerView.ViewHolder> RecyclerView.findFirstVisibleViewHolder() : TViewHolder? {
    val position = (this.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    if (position != RecyclerView.NO_POSITION) {
        @Suppress("UNCHECKED_CAST")
        return this.findViewHolderForAdapterPosition(position) as TViewHolder?
    } else {
        return null
    }
}

fun <TViewHolder : RecyclerView.ViewHolder> RecyclerView.findVisibleViewHolders() : List<TViewHolder> {
    val firstPosition = (this.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    val lastPosition = (this.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    if (firstPosition != RecyclerView.NO_POSITION && lastPosition != RecyclerView.NO_POSITION) {
        return (firstPosition..lastPosition).mapNotNull {
            @Suppress("UNCHECKED_CAST")
            this.findViewHolderForAdapterPosition(it) as TViewHolder?
        }
    } else {
        return emptyList()
    }
}

fun RecyclerView.isScrollIdle() = this.scrollState == RecyclerView.SCROLL_STATE_IDLE

fun Context.getVibrator() = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

/**
 * Bundle builder.
 */
fun bundle(init: Bundle.() -> Unit): Bundle {
    val bundle = Bundle()
    bundle.init()
    return bundle
}

/**
 * Matcher builder.
 */
fun registryMatcher(init: RegistryMatcher.() -> Unit): RegistryMatcher {
    val matcher = RegistryMatcher()
    matcher.init()
    return matcher
}

/**
 * Executes code block with fragment transaction.
 */
fun FragmentManager.transaction(init: FragmentTransaction.() -> Unit) {
    val transaction = beginTransaction()
    transaction.init()
    transaction.commit()
}

/**
 * Anko extension.
 */
inline fun Activity.wearableDrawerLayout(init: WearableDrawerLayout.() -> Unit): WearableDrawerLayout =
    ankoView({ WearableDrawerLayout(it) }, theme = 0, init = init)

/**
 * Anko extension.
 */
inline fun ViewManager.wearableNavigationDrawerView(init: WearableNavigationDrawerView.() -> Unit): WearableNavigationDrawerView =
    ankoView({ WearableNavigationDrawerView(it) }, theme = 0, init = init)

inline fun ViewManager.wearableRecyclerView(theme: Int = 0, init: WearableRecyclerView.() -> Unit): WearableRecyclerView =
    ankoView({ WearableRecyclerView(it) }, theme = theme, init = init)

fun SharedPreferences.edit(init: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.init()
    editor.apply()
}

fun Context.getPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
fun Context.editPreferences(init: SharedPreferences.Editor.() -> Unit) = getPreferences().edit(init)
