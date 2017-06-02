package me.eigenein.nexttrainwear

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.drawer.WearableNavigationDrawer
import android.util.Log
import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter
import me.eigenein.nexttrainwear.fragments.StationsFragment
import me.eigenein.nexttrainwear.fragments.SettingsFragment
import me.eigenein.nexttrainwear.fragments.TrainsFragment
import me.eigenein.nexttrainwear.interfaces.AmbientListenable
import me.eigenein.nexttrainwear.interfaces.AmbientListener
import java.util.concurrent.TimeUnit

class MainActivity :
    WearableActivity(),
    NavigationDrawerAdapter.OnItemSelectedListener,
    AmbientListenable {

    override var ambientListener: AmbientListener? = null

    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val navigationDrawer = findViewById(R.id.navigation_drawer) as WearableNavigationDrawer
        navigationDrawer.setAdapter(NavigationDrawerAdapter(this, this))
        navigationDrawer.setShouldOnlyOpenWhenAtTop(false)

        fragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, TrainsFragment())
            .commit()
    }

    public override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodePermissions)
        }
    }

    override fun onResume() {
        super.onResume()

        /* TODO:
        val timeMillis = System.currentTimeMillis()
        val triggerTimeMillis = timeMillis + displayUpdateIntervalMillis - (timeMillis % displayUpdateIntervalMillis)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            displayUpdateIntervalMillis,
            alarmPendingIntent)
        */
    }

    override fun onPause() {
        super.onPause()
        alarmManager.cancel(alarmPendingIntent)
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        ambientListener?.onEnterAmbient()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ambientListener?.onUpdateDisplay()
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        ambientListener?.onExitAmbient()
    }

    override fun onItemSelected(index: Int) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragments[index]()).commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.w(logTag, "Permission is not granted")
        }
    }

    companion object {

        private const val requestCodePermissions = 1

        private val displayUpdateIntervalMillis = TimeUnit.SECONDS.toMillis(1)
        private val logTag = MainActivity::class.java.simpleName
        private val fragments = arrayOf(
            { TrainsFragment() },
            { StationsFragment() },
            { SettingsFragment() }
        )
    }
}
