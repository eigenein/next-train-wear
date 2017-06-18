package me.eigenein.nexttrainwear

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.drawer.WearableNavigationDrawer
import android.util.Log
import me.eigenein.nexttrainwear.adapters.NavigationDrawerAdapter
import me.eigenein.nexttrainwear.fragments.SettingsFragment
import me.eigenein.nexttrainwear.fragments.StationsFragment
import me.eigenein.nexttrainwear.fragments.TrainsFragment

class MainActivity :
    WearableActivity(),
    NavigationDrawerAdapter.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setAmbientEnabled()

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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        window.decorView.setBackgroundColor(Color.BLACK)
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        window.decorView.setBackgroundResource(R.color.lighter_background)
    }

    override fun onItemSelected(index: Int) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, FRAGMENTS[index]()).commit()
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
