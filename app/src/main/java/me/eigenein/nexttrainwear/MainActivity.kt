package me.eigenein.nexttrainwear

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.app.ActivityCompat
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter
import me.eigenein.nexttrainwear.fragments.SettingsFragment
import me.eigenein.nexttrainwear.fragments.StationsFragment
import me.eigenein.nexttrainwear.fragments.TrainsFragment
import me.eigenein.nexttrainwear.utils.transaction
import me.eigenein.nexttrainwear.utils.wearableDrawerLayout
import me.eigenein.nexttrainwear.utils.wearableNavigationDrawerView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent

class MainActivity : WearableActivity() {

    private lateinit var contentFrame: FrameLayout
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wearableDrawerLayout {
            layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)

            contentFrame = frameLayout {
                id = View.generateViewId()
                layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
            }

            wearableNavigationDrawerView {
                layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
                backgroundResource = R.color.lighter_background
                isOpenOnlyAtTopEnabled = false
                setAdapter(NavigationDrawerAdapter(this@MainActivity))
                addOnItemSelectedListener {
                    fragmentManager.transaction { replace(contentFrame.id, FRAGMENTS[it]()) }
                }
            }
        }

        setAmbientEnabled()

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG)

        fragmentManager.transaction { replace(contentFrame.id, TrainsFragment()) }
    }

    public override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        window.decorView.backgroundColor = Color.BLACK
        wakeLock.acquire() // FIXME: better ideas?
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        window.decorView.backgroundResource = R.color.lighter_background
        wakeLock.release() // FIXME: better ideas?
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.w(LOG_TAG, "Permission is not granted")
        }
    }

    companion object {

        private const val PERMISSIONS_REQUEST_CODE = 1

        private val LOG_TAG = MainActivity::class.java.simpleName
        private val FRAGMENTS = arrayOf(
            { TrainsFragment() },
            { StationsFragment() },
            { SettingsFragment() }
        )
    }
}
